import { z } from "zod";

export const prioritySchema = z.enum(["LOW", "MEDIUM", "HIGH"]);

export const apiErrorSchema = z.object({
  timestamp: z.string(),
  status: z.number(),
  error: z.string(),
  code: z.string().nullable().optional(),
  message: z.string(),
  details: z.record(z.string()).nullable().optional(),
  path: z.string()
});

export const authResponseSchema = z.object({
  userId: z.number(),
  username: z.string(),
  email: z.string().email(),
  createdAt: z.string().nullable(),
  updatedAt: z.string().nullable()
});

export const sectionResponseSchema = z.object({
  sectionId: z.number(),
  sectionName: z.string(),
  createdAt: z.string().nullable(),
  updatedAt: z.string().nullable()
});

export const parentTaskResponseSchema = z
  .object({
    parentTaskId: z.number(),
    parentTaskTitle: z.string(),
    deadline: z.string().nullable(),
    priority: prioritySchema.nullable(),
    isCompleted: z.boolean().optional(),
    completed: z.boolean().optional(),
    createdAt: z.string().nullable(),
    updatedAt: z.string().nullable(),
    completedAt: z.string().nullable()
  })
  .transform((data) => ({
    parentTaskId: data.parentTaskId,
    parentTaskTitle: data.parentTaskTitle,
    deadline: data.deadline,
    priority: data.priority,
    isCompleted: data.isCompleted ?? data.completed ?? false,
    createdAt: data.createdAt ?? null,
    updatedAt: data.updatedAt ?? null,
    completedAt: data.completedAt ?? null
  }));

export const childTaskResponseSchema = z
  .object({
    childTaskId: z.number(),
    childTaskTitle: z.string(),
    deadline: z.string().nullable(),
    priority: prioritySchema.nullable(),
    isCompleted: z.boolean().optional(),
    completed: z.boolean().optional(),
    createdAt: z.string().nullable(),
    updatedAt: z.string().nullable(),
    completedAt: z.string().nullable()
  })
  .transform((data) => ({
    childTaskId: data.childTaskId,
    childTaskTitle: data.childTaskTitle,
    deadline: data.deadline,
    priority: data.priority,
    isCompleted: data.isCompleted ?? data.completed ?? false,
    createdAt: data.createdAt ?? null,
    updatedAt: data.updatedAt ?? null,
    completedAt: data.completedAt ?? null
  }));

export const registerPayloadSchema = z.object({
  email: z.string().email(),
  username: z.string().min(3).max(50).regex(/^[a-zA-Z0-9_-]+$/),
  password: z
    .string()
    .min(8)
    .regex(/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d).*$/)
});

export const loginPayloadSchema = z.object({
  email: z.string().email(),
  password: z.string().min(8)
});

export const sectionPayloadSchema = z.object({
  sectionName: z.string().min(1)
});

export const parentTaskPayloadSchema = z.object({
  parentTaskTitle: z.string().min(1).max(255),
  deadline: z.string().datetime().nullable(),
  priority: prioritySchema,
  isCompleted: z.boolean()
});

export const childTaskPayloadSchema = z.object({
  childTaskTitle: z.string().min(1).max(255),
  deadline: z.string().datetime().nullable(),
  priority: prioritySchema,
  isCompleted: z.boolean()
});

export const sectionListSchema = z.array(sectionResponseSchema);
export const parentTaskListSchema = z.array(parentTaskResponseSchema);
export const childTaskListSchema = z.array(childTaskResponseSchema);
