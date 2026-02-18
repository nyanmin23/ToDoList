import { useEffect, useMemo, useState } from "react";
import { Navigate } from "react-router-dom";
import { z } from "zod";
import { ErrorMessage } from "@/components/error-message";
import { RequestStateChip } from "@/components/request-state-chip";
import { TopNav } from "@/components/top-nav";
import { useAuth } from "@/context/auth-context";
import { useRequestState } from "@/hooks/useRequestState";
import { childTaskApi, parentTaskApi, sectionApi } from "@/lib/api";
import { AppApiError } from "@/lib/http";
import type {
  ChildTaskPayload,
  ChildTaskResponse,
  ParentTaskPayload,
  ParentTaskResponse,
  Priority,
  SectionResponse
} from "@/types/api";

interface TaskCreateDraft {
  title: string;
  deadline: string;
  priority: Priority;
}

interface ParentEditDraft {
  title: string;
  deadline: string;
  priority: Priority;
}

const sectionNameSchema = z.string().trim().min(1, "Section name is required");
const taskTitleSchema = z.string().trim().min(1, "Task title is required").max(255);

function toIsoOrNull(localValue: string): string | null {
  if (!localValue) {
    return null;
  }
  const parsed = new Date(localValue);
  if (Number.isNaN(parsed.getTime())) {
    return null;
  }
  return parsed.toISOString();
}

function toLocalInput(iso: string | null): string {
  if (!iso) {
    return "";
  }
  const parsed = new Date(iso);
  if (Number.isNaN(parsed.getTime())) {
    return "";
  }
  return parsed.toISOString().slice(0, 16);
}

function formatDateLabel(iso: string | null): string {
  if (!iso) {
    return "No deadline";
  }
  const parsed = new Date(iso);
  if (Number.isNaN(parsed.getTime())) {
    return "No deadline";
  }
  return parsed.toLocaleString();
}

function normalizeError(error: unknown): string {
  if (error instanceof AppApiError) {
    return error.message;
  }
  return "Unexpected request failure";
}

export function DashboardPage() {
  const { user, logout, clearSession } = useAuth();
  const [sections, setSections] = useState<SectionResponse[]>([]);
  const [tasksBySection, setTasksBySection] = useState<Record<number, ParentTaskResponse[]>>({});
  const [childByParent, setChildByParent] = useState<Record<number, ChildTaskResponse[]>>({});
  const [expandedParents, setExpandedParents] = useState<Record<number, boolean>>({});

  const [newSectionName, setNewSectionName] = useState<string>("");
  const [newTaskDrafts, setNewTaskDrafts] = useState<Record<number, TaskCreateDraft>>({});
  const [newChildDrafts, setNewChildDrafts] = useState<Record<number, string>>({});

  const [editingSectionId, setEditingSectionId] = useState<number | null>(null);
  const [editingSectionName, setEditingSectionName] = useState<string>("");

  const [editingParentId, setEditingParentId] = useState<number | null>(null);
  const [parentEdits, setParentEdits] = useState<Record<number, ParentEditDraft>>({});

  const [editingChildId, setEditingChildId] = useState<number | null>(null);
  const [editingChildTitle, setEditingChildTitle] = useState<string>("");

  const [loadState, loadActions] = useRequestState("idle");
  const [sectionState, sectionActions] = useRequestState("idle");
  const [taskState, taskActions] = useRequestState("idle");
  const [childState, childActions] = useRequestState("idle");

  useEffect(() => {
    const initialize = async () => {
      loadActions.setLoading();
      try {
        const sectionList = await sectionApi.list();
        setSections(sectionList);

        const entries = await Promise.all(
          sectionList.map(async (section) => {
            const tasks = await parentTaskApi.list(section.sectionId);
            return [section.sectionId, tasks] as const;
          })
        );

        setTasksBySection(Object.fromEntries(entries));
        loadActions.setSuccess();
      } catch (error) {
        loadActions.setError(normalizeError(error));
      }
    };

    void initialize();
  }, []);

  const totalTasks = useMemo<number>(
    () => Object.values(tasksBySection).reduce((sum, tasks) => sum + tasks.length, 0),
    [tasksBySection]
  );

  if (!user) {
    return <Navigate to="/auth" replace />;
  }

  const ensureTaskDraft = (sectionId: number): TaskCreateDraft => {
    return (
      newTaskDrafts[sectionId] ?? {
        title: "",
        deadline: "",
        priority: "LOW"
      }
    );
  };

  const setTaskDraft = (sectionId: number, next: TaskCreateDraft) => {
    setNewTaskDrafts((prev) => ({ ...prev, [sectionId]: next }));
  };

  const createSection = async () => {
    const parsed = sectionNameSchema.safeParse(newSectionName);
    if (!parsed.success) {
      sectionActions.setError(parsed.error.issues[0]?.message ?? "Invalid section");
      return;
    }

    sectionActions.setLoading();
    try {
      const created = await sectionApi.create({ sectionName: parsed.data });
      setSections((prev) => [...prev, created]);
      setTasksBySection((prev) => ({ ...prev, [created.sectionId]: [] }));
      setNewSectionName("");
      sectionActions.setSuccess();
    } catch (error) {
      sectionActions.setError(normalizeError(error));
    }
  };

  const saveSectionRename = async (sectionId: number) => {
    const parsed = sectionNameSchema.safeParse(editingSectionName);
    if (!parsed.success) {
      sectionActions.setError(parsed.error.issues[0]?.message ?? "Invalid section");
      return;
    }

    sectionActions.setLoading();
    try {
      const updated = await sectionApi.update(sectionId, { sectionName: parsed.data });
      setSections((prev) => prev.map((section) => (section.sectionId === sectionId ? updated : section)));
      setEditingSectionId(null);
      setEditingSectionName("");
      sectionActions.setSuccess();
    } catch (error) {
      sectionActions.setError(normalizeError(error));
    }
  };

  const removeSection = async (sectionId: number) => {
    sectionActions.setLoading();
    try {
      await sectionApi.remove(sectionId);
      setSections((prev) => prev.filter((section) => section.sectionId !== sectionId));
      setTasksBySection((prev) => {
        const next = { ...prev };
        delete next[sectionId];
        return next;
      });
      sectionActions.setSuccess();
    } catch (error) {
      sectionActions.setError(normalizeError(error));
    }
  };

  const createParentTask = async (sectionId: number) => {
    const draft = ensureTaskDraft(sectionId);
    const titleParsed = taskTitleSchema.safeParse(draft.title);
    if (!titleParsed.success) {
      taskActions.setError(titleParsed.error.issues[0]?.message ?? "Invalid task");
      return;
    }

    const payload: ParentTaskPayload = {
      parentTaskTitle: titleParsed.data,
      deadline: toIsoOrNull(draft.deadline),
      priority: draft.priority,
      isCompleted: false
    };

    taskActions.setLoading();
    try {
      const created = await parentTaskApi.create(sectionId, payload);
      setTasksBySection((prev) => ({
        ...prev,
        [sectionId]: [created, ...(prev[sectionId] ?? [])]
      }));
      setTaskDraft(sectionId, { title: "", deadline: "", priority: "LOW" });
      taskActions.setSuccess();
    } catch (error) {
      taskActions.setError(normalizeError(error));
    }
  };

  const toggleComplete = async (sectionId: number, task: ParentTaskResponse) => {
    taskActions.setLoading();

    const payload: ParentTaskPayload = {
      parentTaskTitle: task.parentTaskTitle,
      deadline: task.deadline,
      priority: task.priority ?? "LOW",
      isCompleted: !task.isCompleted
    };

    try {
      const updated = await parentTaskApi.update(sectionId, task.parentTaskId, payload);
      setTasksBySection((prev) => ({
        ...prev,
        [sectionId]: (prev[sectionId] ?? []).map((item) =>
          item.parentTaskId === task.parentTaskId ? updated : item
        )
      }));
      taskActions.setSuccess();
    } catch (error) {
      taskActions.setError(normalizeError(error));
    }
  };

  const saveParentEdit = async (sectionId: number, task: ParentTaskResponse) => {
    const edit = parentEdits[task.parentTaskId];
    if (!edit) {
      taskActions.setError("No edit draft for selected task");
      return;
    }

    const titleParsed = taskTitleSchema.safeParse(edit.title);
    if (!titleParsed.success) {
      taskActions.setError(titleParsed.error.issues[0]?.message ?? "Invalid title");
      return;
    }

    taskActions.setLoading();
    const payload: ParentTaskPayload = {
      parentTaskTitle: titleParsed.data,
      deadline: toIsoOrNull(edit.deadline),
      priority: edit.priority,
      isCompleted: task.isCompleted
    };

    try {
      const updated = await parentTaskApi.update(sectionId, task.parentTaskId, payload);
      setTasksBySection((prev) => ({
        ...prev,
        [sectionId]: (prev[sectionId] ?? []).map((item) =>
          item.parentTaskId === task.parentTaskId ? updated : item
        )
      }));
      setEditingParentId(null);
      taskActions.setSuccess();
    } catch (error) {
      taskActions.setError(normalizeError(error));
    }
  };

  const removeParentTask = async (sectionId: number, parentTaskId: number) => {
    taskActions.setLoading();
    try {
      await parentTaskApi.remove(sectionId, parentTaskId);
      setTasksBySection((prev) => ({
        ...prev,
        [sectionId]: (prev[sectionId] ?? []).filter((task) => task.parentTaskId !== parentTaskId)
      }));
      taskActions.setSuccess();
    } catch (error) {
      taskActions.setError(normalizeError(error));
    }
  };

  const toggleChildren = async (sectionId: number, parentTaskId: number) => {
    const expanded = expandedParents[parentTaskId] ?? false;
    setExpandedParents((prev) => ({ ...prev, [parentTaskId]: !expanded }));

    if (!expanded && !childByParent[parentTaskId]) {
      childActions.setLoading();
      try {
        const list = await childTaskApi.list(sectionId, parentTaskId);
        setChildByParent((prev) => ({ ...prev, [parentTaskId]: list }));
        childActions.setSuccess();
      } catch (error) {
        childActions.setError(normalizeError(error));
      }
    }
  };

  const createChildTask = async (sectionId: number, parentTaskId: number) => {
    const draft = newChildDrafts[parentTaskId] ?? "";
    const titleParsed = taskTitleSchema.safeParse(draft);
    if (!titleParsed.success) {
      childActions.setError(titleParsed.error.issues[0]?.message ?? "Invalid subtask");
      return;
    }

    const payload: ChildTaskPayload = {
      childTaskTitle: titleParsed.data,
      deadline: null,
      priority: "LOW",
      isCompleted: false
    };

    childActions.setLoading();
    try {
      const created = await childTaskApi.create(sectionId, parentTaskId, payload);
      setChildByParent((prev) => ({
        ...prev,
        [parentTaskId]: [created, ...(prev[parentTaskId] ?? [])]
      }));
      setNewChildDrafts((prev) => ({ ...prev, [parentTaskId]: "" }));
      childActions.setSuccess();
    } catch (error) {
      childActions.setError(normalizeError(error));
    }
  };

  const saveChildEdit = async (
    sectionId: number,
    parentTaskId: number,
    childTask: ChildTaskResponse
  ) => {
    const titleParsed = taskTitleSchema.safeParse(editingChildTitle);
    if (!titleParsed.success) {
      childActions.setError(titleParsed.error.issues[0]?.message ?? "Invalid subtask");
      return;
    }

    const payload: ChildTaskPayload = {
      childTaskTitle: titleParsed.data,
      deadline: null,
      priority: "LOW",
      isCompleted: false
    };

    childActions.setLoading();
    try {
      const updated = await childTaskApi.update(sectionId, parentTaskId, childTask.childTaskId, payload);
      setChildByParent((prev) => ({
        ...prev,
        [parentTaskId]: (prev[parentTaskId] ?? []).map((item) =>
          item.childTaskId === childTask.childTaskId ? updated : item
        )
      }));
      setEditingChildId(null);
      setEditingChildTitle("");
      childActions.setSuccess();
    } catch (error) {
      childActions.setError(normalizeError(error));
    }
  };

  const removeChildTask = async (sectionId: number, parentTaskId: number, childTaskId: number) => {
    childActions.setLoading();
    try {
      await childTaskApi.remove(sectionId, parentTaskId, childTaskId);
      setChildByParent((prev) => ({
        ...prev,
        [parentTaskId]: (prev[parentTaskId] ?? []).filter((item) => item.childTaskId !== childTaskId)
      }));
      childActions.setSuccess();
    } catch (error) {
      childActions.setError(normalizeError(error));
    }
  };

  return (
    <div>
      <TopNav
        email={user.email}
        onLogout={async () => {
          try {
            await logout();
          } catch {
            clearSession();
          }
        }}
      />

      <main className="mx-auto w-full max-w-5xl px-4 pb-16 pt-10">
        <div className="mb-8 flex flex-wrap items-center justify-between gap-3">
          <div>
            <h1 className="text-4xl font-semibold tracking-tight">Task Workspace</h1>
            <p className="mt-1 text-sm text-app-muted">{totalTasks} tasks synced from backend</p>
          </div>
          <div className="flex gap-2">
            <RequestStateChip status={loadState.status} />
            <RequestStateChip status={sectionState.status} />
            <RequestStateChip status={taskState.status} />
            <RequestStateChip status={childState.status} />
          </div>
        </div>

        <div className="space-y-3">
          <ErrorMessage message={loadState.status === "error" ? loadState.errorMessage : null} />
          <ErrorMessage message={sectionState.status === "error" ? sectionState.errorMessage : null} />
          <ErrorMessage message={taskState.status === "error" ? taskState.errorMessage : null} />
          <ErrorMessage message={childState.status === "error" ? childState.errorMessage : null} />
        </div>

        <section className="mt-6 rounded-xl border border-app-line bg-app-surface p-4 dark:border-app-darkLine dark:bg-app-darkSurface">
          <h2 className="mb-3 text-sm font-semibold uppercase tracking-wide text-app-muted">Create section</h2>
          <div className="flex flex-col gap-2 sm:flex-row">
            <input
              className="w-full rounded-md border border-app-line bg-transparent px-3 py-2 text-sm outline-none focus:border-app-accent dark:border-app-darkLine"
              value={newSectionName}
              onChange={(event) => {
                setNewSectionName(event.target.value);
              }}
              placeholder="Section name"
            />
            <button
              className="rounded-md bg-app-accent px-4 py-2 text-sm font-medium text-white transition hover:bg-app-accentHover"
              onClick={() => {
                void createSection();
              }}
              type="button"
            >
              Add section
            </button>
          </div>
        </section>

        <div className="mt-10 space-y-10">
          {sections.length === 0 && loadState.status !== "loading" ? (
            <p className="text-sm text-app-muted">No sections yet.</p>
          ) : null}

          {sections.map((section) => {
            const tasks = tasksBySection[section.sectionId] ?? [];
            const draft = ensureTaskDraft(section.sectionId);

            return (
              <section key={section.sectionId}>
                <div className="mb-3 flex items-center justify-between gap-3 border-b border-app-line pb-2 dark:border-app-darkLine">
                  <div className="flex items-center gap-2">
                    {editingSectionId === section.sectionId ? (
                      <input
                        className="rounded border border-app-line bg-transparent px-2 py-1 text-base font-semibold outline-none focus:border-app-accent dark:border-app-darkLine"
                        value={editingSectionName}
                        onChange={(event) => {
                          setEditingSectionName(event.target.value);
                        }}
                      />
                    ) : (
                      <h2 className="text-3xl font-semibold tracking-tight">{section.sectionName}</h2>
                    )}
                    <span className="text-sm text-app-muted">{tasks.length}</span>
                  </div>

                  <div className="flex items-center gap-3 text-sm">
                    {editingSectionId === section.sectionId ? (
                      <button
                        className="text-app-accent transition hover:text-app-accentHover"
                        onClick={() => {
                          void saveSectionRename(section.sectionId);
                        }}
                        type="button"
                      >
                        Save
                      </button>
                    ) : (
                      <button
                        className="text-app-muted transition hover:text-app-text dark:hover:text-app-darkText"
                        onClick={() => {
                          setEditingSectionId(section.sectionId);
                          setEditingSectionName(section.sectionName);
                        }}
                        type="button"
                      >
                        Rename
                      </button>
                    )}
                    <button
                      className="text-red-500 transition hover:text-red-600"
                      onClick={() => {
                        void removeSection(section.sectionId);
                      }}
                      type="button"
                    >
                      Delete
                    </button>
                  </div>
                </div>

                <div className="space-y-0.5">
                  {tasks.map((task) => {
                    const edit = parentEdits[task.parentTaskId] ?? {
                      title: task.parentTaskTitle,
                      deadline: toLocalInput(task.deadline),
                      priority: task.priority ?? "LOW"
                    };
                    const children = childByParent[task.parentTaskId] ?? [];
                    const childrenOpen = expandedParents[task.parentTaskId] ?? false;

                    return (
                      <div key={task.parentTaskId} className="border-b border-app-line py-2 dark:border-app-darkLine">
                        <div className="flex flex-col gap-2 sm:flex-row sm:items-center sm:justify-between">
                          <div className="flex items-center gap-3">
                            <input
                              type="checkbox"
                              checked={task.isCompleted}
                              onChange={() => {
                                void toggleComplete(section.sectionId, task);
                              }}
                              className="h-5 w-5 rounded-full border-app-muted text-app-accent focus:ring-app-accent"
                            />

                            {editingParentId === task.parentTaskId ? (
                              <input
                                className="w-72 rounded border border-app-line bg-transparent px-2 py-1 text-sm outline-none focus:border-app-accent dark:border-app-darkLine"
                                value={edit.title}
                                onChange={(event) => {
                                  const next = event.target.value;
                                  setParentEdits((prev) => ({
                                    ...prev,
                                    [task.parentTaskId]: { ...edit, title: next }
                                  }));
                                }}
                              />
                            ) : (
                              <span className={`text-lg ${task.isCompleted ? "text-app-muted line-through" : ""}`}>
                                {task.parentTaskTitle}
                              </span>
                            )}
                          </div>

                          <div className="flex flex-wrap items-center gap-3 text-sm">
                            {editingParentId === task.parentTaskId ? (
                              <>
                                <input
                                  type="datetime-local"
                                  className="rounded border border-app-line bg-transparent px-2 py-1 outline-none focus:border-app-accent dark:border-app-darkLine"
                                  value={edit.deadline}
                                  onChange={(event) => {
                                    const next = event.target.value;
                                    setParentEdits((prev) => ({
                                      ...prev,
                                      [task.parentTaskId]: { ...edit, deadline: next }
                                    }));
                                  }}
                                />
                                <select
                                  className="rounded border border-app-line bg-transparent px-2 py-1 outline-none focus:border-app-accent dark:border-app-darkLine"
                                  value={edit.priority}
                                  onChange={(event) => {
                                    const next = event.target.value as Priority;
                                    setParentEdits((prev) => ({
                                      ...prev,
                                      [task.parentTaskId]: { ...edit, priority: next }
                                    }));
                                  }}
                                >
                                  <option value="LOW">LOW</option>
                                  <option value="MEDIUM">MEDIUM</option>
                                  <option value="HIGH">HIGH</option>
                                </select>
                                <button
                                  className="text-app-accent transition hover:text-app-accentHover"
                                  onClick={() => {
                                    void saveParentEdit(section.sectionId, task);
                                  }}
                                  type="button"
                                >
                                  Save
                                </button>
                              </>
                            ) : (
                              <>
                                <span className="text-app-muted">{formatDateLabel(task.deadline)}</span>
                                <span className="rounded-full bg-gray-100 px-2 py-0.5 text-xs dark:bg-zinc-800">
                                  {task.priority ?? "LOW"}
                                </span>
                                <button
                                  className="text-app-muted transition hover:text-app-text dark:hover:text-app-darkText"
                                  onClick={() => {
                                    setEditingParentId(task.parentTaskId);
                                    setParentEdits((prev) => ({
                                      ...prev,
                                      [task.parentTaskId]: {
                                        title: task.parentTaskTitle,
                                        deadline: toLocalInput(task.deadline),
                                        priority: task.priority ?? "LOW"
                                      }
                                    }));
                                  }}
                                  type="button"
                                >
                                  Edit
                                </button>
                              </>
                            )}

                            <button
                              className="text-red-500 transition hover:text-red-600"
                              onClick={() => {
                                void removeParentTask(section.sectionId, task.parentTaskId);
                              }}
                              type="button"
                            >
                              Delete
                            </button>

                            <button
                              className="text-app-muted transition hover:text-app-text dark:hover:text-app-darkText"
                              onClick={() => {
                                void toggleChildren(section.sectionId, task.parentTaskId);
                              }}
                              type="button"
                            >
                              {childrenOpen ? "Hide" : "Subtasks"}
                            </button>
                          </div>
                        </div>

                        {childrenOpen ? (
                          <div className="mt-3 space-y-2 rounded border border-app-line bg-app-surface p-3 dark:border-app-darkLine dark:bg-app-darkSurface">
                            <div className="flex flex-col gap-2 sm:flex-row">
                              <input
                                className="w-full rounded-md border border-app-line bg-transparent px-3 py-2 text-sm outline-none focus:border-app-accent dark:border-app-darkLine"
                                placeholder="Add subtask"
                                value={newChildDrafts[task.parentTaskId] ?? ""}
                                onChange={(event) => {
                                  setNewChildDrafts((prev) => ({
                                    ...prev,
                                    [task.parentTaskId]: event.target.value
                                  }));
                                }}
                              />
                              <button
                                className="rounded-md border border-app-line px-3 py-2 text-sm transition hover:bg-gray-50 dark:border-app-darkLine dark:hover:bg-zinc-800"
                                onClick={() => {
                                  void createChildTask(section.sectionId, task.parentTaskId);
                                }}
                                type="button"
                              >
                                Add
                              </button>
                            </div>

                            <div className="space-y-1">
                              {children.map((child) => (
                                <div
                                  key={child.childTaskId}
                                  className="flex items-center justify-between border-b border-app-line py-1.5 text-sm dark:border-app-darkLine"
                                >
                                  {editingChildId === child.childTaskId ? (
                                    <input
                                      className="w-72 rounded border border-app-line bg-transparent px-2 py-1 outline-none focus:border-app-accent dark:border-app-darkLine"
                                      value={editingChildTitle}
                                      onChange={(event) => {
                                        setEditingChildTitle(event.target.value);
                                      }}
                                    />
                                  ) : (
                                    <span>{child.childTaskTitle}</span>
                                  )}
                                  <div className="flex items-center gap-3">
                                    {editingChildId === child.childTaskId ? (
                                      <button
                                        className="text-app-accent transition hover:text-app-accentHover"
                                        onClick={() => {
                                          void saveChildEdit(section.sectionId, task.parentTaskId, child);
                                        }}
                                        type="button"
                                      >
                                        Save
                                      </button>
                                    ) : (
                                      <button
                                        className="text-app-muted transition hover:text-app-text dark:hover:text-app-darkText"
                                        onClick={() => {
                                          setEditingChildId(child.childTaskId);
                                          setEditingChildTitle(child.childTaskTitle);
                                        }}
                                        type="button"
                                      >
                                        Edit
                                      </button>
                                    )}
                                    <button
                                      className="text-red-500 transition hover:text-red-600"
                                      onClick={() => {
                                        void removeChildTask(section.sectionId, task.parentTaskId, child.childTaskId);
                                      }}
                                      type="button"
                                    >
                                      Delete
                                    </button>
                                  </div>
                                </div>
                              ))}
                            </div>
                          </div>
                        ) : null}
                      </div>
                    );
                  })}
                </div>

                <div className="mt-3 flex flex-col gap-2 sm:flex-row">
                  <input
                    className="w-full rounded-md border border-app-line bg-transparent px-3 py-2 text-sm outline-none focus:border-app-accent dark:border-app-darkLine"
                    value={draft.title}
                    onChange={(event) => {
                      setTaskDraft(section.sectionId, { ...draft, title: event.target.value });
                    }}
                    placeholder="Add task"
                  />
                  <input
                    type="datetime-local"
                    className="rounded-md border border-app-line bg-transparent px-3 py-2 text-sm outline-none focus:border-app-accent dark:border-app-darkLine"
                    value={draft.deadline}
                    onChange={(event) => {
                      setTaskDraft(section.sectionId, { ...draft, deadline: event.target.value });
                    }}
                  />
                  <select
                    className="rounded-md border border-app-line bg-transparent px-3 py-2 text-sm outline-none focus:border-app-accent dark:border-app-darkLine"
                    value={draft.priority}
                    onChange={(event) => {
                      setTaskDraft(section.sectionId, {
                        ...draft,
                        priority: event.target.value as Priority
                      });
                    }}
                  >
                    <option value="LOW">LOW</option>
                    <option value="MEDIUM">MEDIUM</option>
                    <option value="HIGH">HIGH</option>
                  </select>
                  <button
                    className="rounded-md bg-app-accent px-4 py-2 text-sm font-medium text-white transition hover:bg-app-accentHover"
                    onClick={() => {
                      void createParentTask(section.sectionId);
                    }}
                    type="button"
                  >
                    + Add task
                  </button>
                </div>
              </section>
            );
          })}
        </div>
      </main>
    </div>
  );
}
