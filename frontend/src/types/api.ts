export type Priority = "LOW" | "MEDIUM" | "HIGH";

export interface ApiErrorResponse {
  timestamp: string;
  status: number;
  error: string;
  code: string | null;
  message: string;
  details: Record<string, string> | null;
  path: string;
}

export interface AuthResponse {
  userId: number;
  username: string;
  email: string;
  createdAt: string | null;
  updatedAt: string | null;
}

export interface SectionResponse {
  sectionId: number;
  sectionName: string;
  createdAt: string | null;
  updatedAt: string | null;
}

export interface ParentTaskResponse {
  parentTaskId: number;
  parentTaskTitle: string;
  deadline: string | null;
  priority: Priority | null;
  isCompleted: boolean;
  createdAt: string | null;
  updatedAt: string | null;
  completedAt: string | null;
}

export interface ChildTaskResponse {
  childTaskId: number;
  childTaskTitle: string;
  deadline: string | null;
  priority: Priority | null;
  isCompleted: boolean;
  createdAt: string;
  updatedAt: string;
  completedAt: string | null;
}

export interface RegisterPayload {
  email: string;
  username: string;
  password: string;
}

export interface LoginPayload {
  email: string;
  password: string;
}

export interface SectionPayload {
  sectionName: string;
}

export interface ParentTaskPayload {
  parentTaskTitle: string;
  deadline: string | null;
  priority: Priority;
  isCompleted: boolean;
}

export interface ChildTaskPayload {
  childTaskTitle: string;
  deadline: string | null;
  priority: Priority;
  isCompleted: boolean;
}
