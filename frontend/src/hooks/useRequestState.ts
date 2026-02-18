import { useState } from "react";

export type RequestStatus = "idle" | "loading" | "success" | "error";

export interface RequestState {
  status: RequestStatus;
  errorMessage: string | null;
}

export interface RequestStateActions {
  setLoading: () => void;
  setSuccess: () => void;
  setError: (message: string) => void;
  reset: () => void;
}

export function useRequestState(initialStatus: RequestStatus = "idle"): [RequestState, RequestStateActions] {
  const [state, setState] = useState<RequestState>({
    status: initialStatus,
    errorMessage: null
  });

  const actions: RequestStateActions = {
    setLoading: () => {
      setState({ status: "loading", errorMessage: null });
    },
    setSuccess: () => {
      setState({ status: "success", errorMessage: null });
    },
    setError: (message: string) => {
      setState({ status: "error", errorMessage: message });
    },
    reset: () => {
      setState({ status: "idle", errorMessage: null });
    }
  };

  return [state, actions];
}
