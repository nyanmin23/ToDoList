export function ErrorMessage({ message }: { message: string | null }) {
  if (!message) {
    return null;
  }

  return (
    <p className="rounded-md border border-red-200 bg-red-50 px-3 py-2 text-sm text-red-700 dark:border-red-900 dark:bg-red-900/20 dark:text-red-300">
      {message}
    </p>
  );
}
