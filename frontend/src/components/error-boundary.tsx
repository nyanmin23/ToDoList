import { Component, type ErrorInfo, type ReactNode } from "react";

interface ErrorBoundaryProps {
  children: ReactNode;
}

interface ErrorBoundaryState {
  hasError: boolean;
}

export class ErrorBoundary extends Component<ErrorBoundaryProps, ErrorBoundaryState> {
  constructor(props: ErrorBoundaryProps) {
    super(props);
    this.state = { hasError: false };
  }

  static getDerivedStateFromError(): ErrorBoundaryState {
    return { hasError: true };
  }

  componentDidCatch(error: Error, errorInfo: ErrorInfo) {
    console.error("Application crash captured", error, errorInfo);
  }

  render() {
    if (this.state.hasError) {
      return (
        <main className="min-h-screen bg-app-bg px-4 py-10 text-app-text dark:bg-app-darkBg dark:text-app-darkText">
          <div className="mx-auto max-w-2xl rounded-lg border border-app-line bg-app-surface p-6 dark:border-app-darkLine dark:bg-app-darkSurface">
            <h1 className="text-xl font-semibold">Unexpected UI error</h1>
            <p className="mt-2 text-sm text-app-muted">Refresh this page and try again.</p>
          </div>
        </main>
      );
    }

    return this.props.children;
  }
}
