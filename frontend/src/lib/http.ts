import axios, {
  AxiosError,
  type AxiosInstance,
  type InternalAxiosRequestConfig
} from "axios";
import { ZodError, type ZodType, type ZodTypeDef } from "zod";
import { apiErrorSchema } from "@/schemas/api";
import { emitServerError, emitUnauthorized } from "@/lib/events";

export class AppApiError extends Error {
  status: number;
  code: string | null;
  details: Record<string, string> | null;
  path: string;

  constructor(
    message: string,
    status: number,
    code: string | null,
    details: Record<string, string> | null,
    path: string
  ) {
    super(message);
    this.status = status;
    this.code = code;
    this.details = details;
    this.path = path;
  }
}

function readCsrfToken(): string | null {
  const match = document.cookie.match(/(?:^|;\s*)XSRF-TOKEN=([^;]+)/);
  const token = match?.[1];
  return token ? decodeURIComponent(token) : null;
}

function withCsrf(config: InternalAxiosRequestConfig): InternalAxiosRequestConfig {
  const method = (config.method ?? "get").toLowerCase();
  if (method !== "get" && method !== "head") {
    const token = readCsrfToken();
    if (token) {
      config.headers["X-XSRF-TOKEN"] = token;
    }
  }
  return config;
}

function normalizeError(error: unknown): AppApiError {
  if (error instanceof AppApiError) {
    return error;
  }

  if (error instanceof ZodError) {
    return new AppApiError("Invalid API response shape", 500, "SCHEMA_PARSE_ERROR", null, "");
  }

  if (axios.isAxiosError(error)) {
    const axiosError = error as AxiosError<unknown>;
    const status = axiosError.response?.status ?? 500;
    const parsed = apiErrorSchema.safeParse(axiosError.response?.data);

    if (status === 401) {
      emitUnauthorized();
    }
    if (status >= 500) {
      emitServerError("Server error. Please try again.");
    }

    if (parsed.success) {
      return new AppApiError(
        parsed.data.message,
        parsed.data.status,
        parsed.data.code,
        parsed.data.details,
        parsed.data.path
      );
    }

    return new AppApiError(axiosError.message || "Network error", status, null, null, "");
  }

  return new AppApiError("Unknown error", 500, null, null, "");
}

export const http: AxiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  withCredentials: true,
  headers: {
    "Content-Type": "application/json"
  }
});

http.interceptors.request.use((config) => withCsrf(config));

http.interceptors.response.use(
  (response) => response,
  (error: unknown) => Promise.reject(normalizeError(error))
);

export async function getValidated<T>(url: string, schema: ZodType<T, ZodTypeDef, unknown>): Promise<T> {
  const response = await http.get<unknown>(url);
  return schema.parse(response.data);
}

export async function postValidated<TPayload, TResponse>(
  url: string,
  payload: TPayload,
  payloadSchema: ZodType<TPayload>,
  responseSchema: ZodType<TResponse, ZodTypeDef, unknown>
): Promise<TResponse> {
  const validatedPayload = payloadSchema.parse(payload);
  const response = await http.post<unknown>(url, validatedPayload);
  return responseSchema.parse(response.data);
}

export async function postNoContent<TPayload>(
  url: string,
  payload: TPayload,
  payloadSchema: ZodType<TPayload>
): Promise<void> {
  const validatedPayload = payloadSchema.parse(payload);
  await http.post(url, validatedPayload);
}

export async function putValidated<TPayload, TResponse>(
  url: string,
  payload: TPayload,
  payloadSchema: ZodType<TPayload>,
  responseSchema: ZodType<TResponse, ZodTypeDef, unknown>
): Promise<TResponse> {
  const validatedPayload = payloadSchema.parse(payload);
  const response = await http.put<unknown>(url, validatedPayload);
  return responseSchema.parse(response.data);
}

export async function deleteValidated(url: string): Promise<void> {
  await http.delete(url);
}
