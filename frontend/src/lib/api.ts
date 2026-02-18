import {
  authResponseSchema,
  childTaskListSchema,
  childTaskPayloadSchema,
  childTaskResponseSchema,
  loginPayloadSchema,
  parentTaskListSchema,
  parentTaskPayloadSchema,
  parentTaskResponseSchema,
  registerPayloadSchema,
  sectionListSchema,
  sectionPayloadSchema,
  sectionResponseSchema
} from "@/schemas/api";
import type {
  AuthResponse,
  ChildTaskPayload,
  ChildTaskResponse,
  LoginPayload,
  ParentTaskPayload,
  ParentTaskResponse,
  RegisterPayload,
  SectionPayload,
  SectionResponse
} from "@/types/api";
import { deleteValidated, getValidated, postNoContent, postValidated, putValidated } from "@/lib/http";
import { z } from "zod";

export const authApi = {
  register(payload: RegisterPayload): Promise<AuthResponse> {
    return postValidated("/api/auth/register", payload, registerPayloadSchema, authResponseSchema);
  },
  login(payload: LoginPayload): Promise<AuthResponse> {
    return postValidated("/api/auth/login", payload, loginPayloadSchema, authResponseSchema);
  },
  logout(): Promise<void> {
    return postNoContent("/api/auth/logout", {}, z.object({}));
  }
};

export const sectionApi = {
  list(): Promise<SectionResponse[]> {
    return getValidated("/api/sections", sectionListSchema);
  },
  create(payload: SectionPayload): Promise<SectionResponse> {
    return postValidated("/api/sections", payload, sectionPayloadSchema, sectionResponseSchema);
  },
  update(sectionId: number, payload: SectionPayload): Promise<SectionResponse> {
    return putValidated(`/api/sections/${sectionId}`, payload, sectionPayloadSchema, sectionResponseSchema);
  },
  remove(sectionId: number): Promise<void> {
    return deleteValidated(`/api/sections/${sectionId}`);
  }
};

export const parentTaskApi = {
  list(sectionId: number): Promise<ParentTaskResponse[]> {
    return getValidated(`/api/sections/${sectionId}/parent-tasks`, parentTaskListSchema);
  },
  create(sectionId: number, payload: ParentTaskPayload): Promise<ParentTaskResponse> {
    return postValidated(
      `/api/sections/${sectionId}/parent-tasks`,
      payload,
      parentTaskPayloadSchema,
      parentTaskResponseSchema
    );
  },
  update(sectionId: number, parentTaskId: number, payload: ParentTaskPayload): Promise<ParentTaskResponse> {
    return putValidated(
      `/api/sections/${sectionId}/parent-tasks/${parentTaskId}`,
      payload,
      parentTaskPayloadSchema,
      parentTaskResponseSchema
    );
  },
  remove(sectionId: number, parentTaskId: number): Promise<void> {
    return deleteValidated(`/api/sections/${sectionId}/parent-tasks/${parentTaskId}`);
  }
};

export const childTaskApi = {
  list(sectionId: number, parentTaskId: number): Promise<ChildTaskResponse[]> {
    return getValidated(
      `/api/sections/${sectionId}/parent-tasks/${parentTaskId}/child-tasks`,
      childTaskListSchema
    );
  },
  create(sectionId: number, parentTaskId: number, payload: ChildTaskPayload): Promise<ChildTaskResponse> {
    return postValidated(
      `/api/sections/${sectionId}/parent-tasks/${parentTaskId}/child-tasks`,
      payload,
      childTaskPayloadSchema,
      childTaskResponseSchema
    );
  },
  update(
    sectionId: number,
    parentTaskId: number,
    childTaskId: number,
    payload: ChildTaskPayload
  ): Promise<ChildTaskResponse> {
    return putValidated(
      `/api/sections/${sectionId}/parent-tasks/${parentTaskId}/child-tasks/${childTaskId}`,
      payload,
      childTaskPayloadSchema,
      childTaskResponseSchema
    );
  },
  remove(sectionId: number, parentTaskId: number, childTaskId: number): Promise<void> {
    return deleteValidated(
      `/api/sections/${sectionId}/parent-tasks/${parentTaskId}/child-tasks/${childTaskId}`
    );
  }
};