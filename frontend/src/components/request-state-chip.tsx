import type { RequestStatus } from "@/hooks/useRequestState";

export function RequestStateChip({ status }: { status: RequestStatus }) {
  if (status === "idle") {
    return null;
  }

  if (status === "loading") {
    return (
      <span className="rounded-full bg-amber-100 px-2 py-1 text-xs font-medium text-amber-800 dark:bg-amber-900/40 dark:text-amber-300">
        Loading
      </span>
    );
  }

  if (status === "error") {
    return (
      <span className="rounded-full bg-red-100 px-2 py-1 text-xs font-medium text-red-700 dark:bg-red-900/40 dark:text-red-300">
        Error
      </span>
    );
  }

  return (
    <span className="rounded-full bg-emerald-100 px-2 py-1 text-xs font-medium text-emerald-700 dark:bg-emerald-900/40 dark:text-emerald-300">
      Synced
    </span>
  );
}
