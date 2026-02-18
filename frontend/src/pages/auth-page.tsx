import { useMemo, useState } from "react";
import { Navigate } from "react-router-dom";
import { z } from "zod";
import { ErrorMessage } from "@/components/error-message";
import { RequestStateChip } from "@/components/request-state-chip";
import { TopNav } from "@/components/top-nav";
import { useAuth } from "@/context/auth-context";
import { useRequestState } from "@/hooks/useRequestState";
import type { LoginPayload, RegisterPayload } from "@/types/api";
import { AppApiError } from "@/lib/http";

const loginUiSchema = z.object({
  email: z.string().email("Enter a valid email"),
  password: z.string().min(8, "Password must be at least 8 characters")
});

const registerUiSchema = z.object({
  email: z.string().email("Enter a valid email"),
  username: z
    .string()
    .min(3, "Username must be at least 3 characters")
    .regex(/^[a-zA-Z0-9_-]+$/, "Username can only contain letters, numbers, hyphens, underscores"),
  password: z
    .string()
    .min(8, "Password must be at least 8 characters")
    .regex(/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d).*$/, "Password needs upper, lower, and number")
});

type FormMode = "login" | "register";

export function AuthPage() {
  const { user, login, register } = useAuth();
  const [mode, setMode] = useState<FormMode>("login");
  const [loginData, setLoginData] = useState<LoginPayload>({ email: "", password: "" });
  const [registerData, setRegisterData] = useState<RegisterPayload>({
    email: "",
    username: "",
    password: ""
  });
  const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({});
  const [submitState, submitActions] = useRequestState("idle");

  const actionLabel = useMemo(() => (mode === "login" ? "Login" : "Register"), [mode]);

  if (user) {
    return <Navigate to="/" replace />;
  }

  const submit = async () => {
    submitActions.setLoading();
    setFieldErrors({});

    if (mode === "login") {
      const parsed = loginUiSchema.safeParse(loginData);
      if (!parsed.success) {
        const flattened = parsed.error.flatten().fieldErrors;
        setFieldErrors({
          email: flattened.email?.[0] ?? "",
          password: flattened.password?.[0] ?? ""
        });
        submitActions.setError("Validation failed");
        return;
      }

      try {
        await login(parsed.data);
        submitActions.setSuccess();
      } catch (error) {
        const message = error instanceof AppApiError ? error.message : "Request failed";
        submitActions.setError(message);
      }
      return;
    }

    const parsed = registerUiSchema.safeParse(registerData);
    if (!parsed.success) {
      const flattened = parsed.error.flatten().fieldErrors;
      setFieldErrors({
        email: flattened.email?.[0] ?? "",
        username: flattened.username?.[0] ?? "",
        password: flattened.password?.[0] ?? ""
      });
      submitActions.setError("Validation failed");
      return;
    }

    try {
      await register(parsed.data);
      submitActions.setSuccess();
    } catch (error) {
      const message = error instanceof AppApiError ? error.message : "Request failed";
      submitActions.setError(message);
    }
  };

  return (
    <div>
      <TopNav />
      <main className="mx-auto mt-14 w-full max-w-md px-4 pb-16">
        <div className="rounded-xl border border-app-line bg-app-surface p-6 shadow-sm dark:border-app-darkLine dark:bg-app-darkSurface">
          <div className="mb-5 flex items-center justify-between">
            <div className="inline-flex rounded-full bg-gray-100 p-1 dark:bg-zinc-800">
              <button
                className={`rounded-full px-4 py-1.5 text-sm ${
                  mode === "login"
                    ? "bg-white text-app-text shadow-sm dark:bg-zinc-900 dark:text-app-darkText"
                    : "text-app-muted"
                }`}
                onClick={() => setMode("login")}
                type="button"
              >
                Login
              </button>
              <button
                className={`rounded-full px-4 py-1.5 text-sm ${
                  mode === "register"
                    ? "bg-white text-app-text shadow-sm dark:bg-zinc-900 dark:text-app-darkText"
                    : "text-app-muted"
                }`}
                onClick={() => setMode("register")}
                type="button"
              >
                Register
              </button>
            </div>
            <RequestStateChip status={submitState.status} />
          </div>

          <div className="space-y-3">
            <div>
              <label className="mb-1 block text-xs uppercase tracking-wide text-app-muted">Email</label>
              <input
                className="w-full rounded-md border border-app-line bg-transparent px-3 py-2 text-sm outline-none focus:border-app-accent dark:border-app-darkLine"
                value={mode === "login" ? loginData.email : registerData.email}
                onChange={(event) => {
                  const next = event.target.value;
                  if (mode === "login") {
                    setLoginData((prev) => ({ ...prev, email: next }));
                  } else {
                    setRegisterData((prev) => ({ ...prev, email: next }));
                  }
                }}
                type="email"
              />
              {fieldErrors.email ? <p className="mt-1 text-xs text-red-500">{fieldErrors.email}</p> : null}
            </div>

            {mode === "register" ? (
              <div>
                <label className="mb-1 block text-xs uppercase tracking-wide text-app-muted">Username</label>
                <input
                  className="w-full rounded-md border border-app-line bg-transparent px-3 py-2 text-sm outline-none focus:border-app-accent dark:border-app-darkLine"
                  value={registerData.username}
                  onChange={(event) => {
                    setRegisterData((prev) => ({ ...prev, username: event.target.value }));
                  }}
                  type="text"
                />
                {fieldErrors.username ? <p className="mt-1 text-xs text-red-500">{fieldErrors.username}</p> : null}
              </div>
            ) : null}

            <div>
              <label className="mb-1 block text-xs uppercase tracking-wide text-app-muted">Password</label>
              <input
                className="w-full rounded-md border border-app-line bg-transparent px-3 py-2 text-sm outline-none focus:border-app-accent dark:border-app-darkLine"
                value={mode === "login" ? loginData.password : registerData.password}
                onChange={(event) => {
                  const next = event.target.value;
                  if (mode === "login") {
                    setLoginData((prev) => ({ ...prev, password: next }));
                  } else {
                    setRegisterData((prev) => ({ ...prev, password: next }));
                  }
                }}
                type="password"
              />
              {fieldErrors.password ? <p className="mt-1 text-xs text-red-500">{fieldErrors.password}</p> : null}
            </div>

            <button
              className="w-full rounded-md bg-app-accent px-3 py-2 text-sm font-semibold text-white transition hover:bg-app-accentHover disabled:cursor-not-allowed disabled:opacity-60"
              onClick={() => {
                void submit();
              }}
              disabled={submitState.status === "loading"}
              type="button"
            >
              {submitState.status === "loading" ? "Submitting..." : actionLabel}
            </button>

            <ErrorMessage message={submitState.status === "error" ? submitState.errorMessage : null} />
          </div>
        </div>
      </main>
    </div>
  );
}
