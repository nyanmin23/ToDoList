export const UNAUTHORIZED_EVENT = "app:unauthorized";
export const SERVER_ERROR_EVENT = "app:server-error";

export function emitUnauthorized() {
  window.dispatchEvent(new CustomEvent(UNAUTHORIZED_EVENT));
}

export function emitServerError(message: string) {
  window.dispatchEvent(new CustomEvent<string>(SERVER_ERROR_EVENT, { detail: message }));
}
