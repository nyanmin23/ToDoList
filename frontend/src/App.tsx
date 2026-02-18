import { useEffect, useState } from "react";
import { Navigate, Route, Routes, useLocation } from "react-router-dom";
import { AuthPage } from "@/pages/auth-page";
import { DashboardPage } from "@/pages/dashboard-page";
import { useAuth } from "@/context/auth-context";
import { SERVER_ERROR_EVENT } from "@/lib/events";

function ProtectedRoute({ children }: { children: JSX.Element }) {
  const { user } = useAuth();
  const location = useLocation();

  if (!user) {
    return <Navigate to="/auth" replace state={{ from: location.pathname }} />;
  }

  return children;
}

export function App() {
  const [globalServerMessage, setGlobalServerMessage] = useState<string | null>(null);

  useEffect(() => {
    const handler = (event: Event) => {
      const custom = event as CustomEvent<string>;
      setGlobalServerMessage(custom.detail);
    };
    window.addEventListener(SERVER_ERROR_EVENT, handler);

    return () => {
      window.removeEventListener(SERVER_ERROR_EVENT, handler);
    };
  }, []);

  return (
    <div className="min-h-screen bg-app-bg text-app-text dark:bg-app-darkBg dark:text-app-darkText">
      {globalServerMessage ? (
        <div className="border-b border-red-300 bg-red-50 px-4 py-2 text-sm text-red-700 dark:border-red-800 dark:bg-red-900/20 dark:text-red-300">
          {globalServerMessage}
        </div>
      ) : null}
      <Routes>
        <Route path="/auth" element={<AuthPage />} />
        <Route
          path="/"
          element={
            <ProtectedRoute>
              <DashboardPage />
            </ProtectedRoute>
          }
        />
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </div>
  );
}
