import { useTheme } from "@/context/theme-context";

interface TopNavProps {
  email?: string;
  onLogout?: () => Promise<void>;
}

export function TopNav({ email, onLogout }: TopNavProps) {
  const { theme, toggleTheme } = useTheme();

  return (
    <header className="sticky top-0 z-10 border-b border-black/10 bg-app-accent text-white">
      <div className="mx-auto flex w-full max-w-5xl items-center justify-between px-4 py-3">
        <div className="flex items-center gap-3">
          <div className="rounded bg-white px-2 py-1 text-xs font-bold uppercase tracking-wide text-app-accent">td</div>
          <span className="text-2xl font-semibold tracking-tight">todoist</span>
        </div>
        <div className="flex items-center gap-2 text-sm">
          {email ? <span className="hidden text-white/90 sm:block">{email}</span> : null}
          <button
            className="rounded border border-white/40 px-3 py-1.5 transition hover:bg-white/10"
            onClick={toggleTheme}
            type="button"
          >
            {theme === "dark" ? "Light" : "Dark"}
          </button>
          {onLogout ? (
            <button
              className="rounded border border-white px-3 py-1.5 transition hover:bg-white/10"
              onClick={() => {
                void onLogout();
              }}
              type="button"
            >
              Logout
            </button>
          ) : null}
        </div>
      </div>
    </header>
  );
}
