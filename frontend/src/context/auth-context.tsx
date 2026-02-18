import {
  createContext,
  useCallback,
  useContext,
  useEffect,
  useMemo,
  useState,
  type ReactNode
} from "react";
import { authApi } from "@/lib/api";
import type { AuthResponse, LoginPayload, RegisterPayload } from "@/types/api";
import { UNAUTHORIZED_EVENT } from "@/lib/events";

interface AuthContextValue {
  user: AuthResponse | null;
  login: (payload: LoginPayload) => Promise<AuthResponse>;
  register: (payload: RegisterPayload) => Promise<AuthResponse>;
  logout: () => Promise<void>;
  clearSession: () => void;
}

const STORAGE_KEY = "auth-user";
const AuthContext = createContext<AuthContextValue | null>(null);

function readStoredUser(): AuthResponse | null {
  const raw = localStorage.getItem(STORAGE_KEY);
  if (!raw) {
    return null;
  }
  try {
    return JSON.parse(raw) as AuthResponse;
  } catch {
    return null;
  }
}

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<AuthResponse | null>(() => readStoredUser());

  const setSession = useCallback((nextUser: AuthResponse | null) => {
    setUser(nextUser);
    if (nextUser) {
      localStorage.setItem(STORAGE_KEY, JSON.stringify(nextUser));
    } else {
      localStorage.removeItem(STORAGE_KEY);
    }
  }, []);

  useEffect(() => {
    const onUnauthorized = () => {
      setSession(null);
    };

    window.addEventListener(UNAUTHORIZED_EVENT, onUnauthorized);
    return () => {
      window.removeEventListener(UNAUTHORIZED_EVENT, onUnauthorized);
    };
  }, [setSession]);

  const login = useCallback(
    async (payload: LoginPayload) => {
      const response = await authApi.login(payload);
      setSession(response);
      return response;
    },
    [setSession]
  );

  const register = useCallback(
    async (payload: RegisterPayload) => {
      const response = await authApi.register(payload);
      setSession(response);
      return response;
    },
    [setSession]
  );

  const logout = useCallback(async () => {
    await authApi.logout();
    setSession(null);
  }, [setSession]);

  const clearSession = useCallback(() => {
    setSession(null);
  }, [setSession]);

  const value = useMemo<AuthContextValue>(
    () => ({ user, login, register, logout, clearSession }),
    [user, login, register, logout, clearSession]
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth(): AuthContextValue {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error("useAuth must be used inside AuthProvider");
  }
  return context;
}
