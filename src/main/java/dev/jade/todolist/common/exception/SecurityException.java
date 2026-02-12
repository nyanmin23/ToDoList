package dev.jade.todolist.common.exception;

/**
 * Base exception for security-related errors (authentication and authorization).
 */
public abstract class SecurityException extends ToDoListApplicationException {

    protected SecurityException(
            String errorCode,
            String userMessage,
            String developerMessage
    ) {
        super(errorCode, userMessage, developerMessage);
    }

    protected SecurityException(
            String errorCode,
            String userMessage,
            String developerMessage,
            Throwable cause
    ) {
        super(errorCode, userMessage, developerMessage, cause);
    }
}

/**
 * Base exception for authentication failures.
 * HTTP Status: 401 UNAUTHORIZED
 */
abstract class AuthenticationException extends SecurityException {

    protected AuthenticationException(
            String errorCode,
            String userMessage,
            String developerMessage
    ) {
        super(errorCode, userMessage, developerMessage);
    }
}

/**
 * Exception thrown when login credentials are invalid.
 * HTTP Status: 401 UNAUTHORIZED
 */
class InvalidCredentialsException extends AuthenticationException {

    private static final String ERROR_CODE = "INVALID_CREDENTIALS";

    public InvalidCredentialsException(String email) {
        super(
                ERROR_CODE,
                "Invalid email or password.",
                String.format("Authentication failed for email: %s", email)
        );
    }
}

/**
 * Base exception for authorization failures.
 * HTTP Status: 403 FORBIDDEN
 */
abstract class AuthorizationException extends SecurityException {

    protected AuthorizationException(
            String errorCode,
            String userMessage,
            String developerMessage
    ) {
        super(errorCode, userMessage, developerMessage);
    }
}

/**
 * Exception thrown when an operation is forbidden for the authenticated user.
 * HTTP Status: 403 FORBIDDEN
 */
class ForbiddenOperationException extends AuthorizationException {

    private static final String ERROR_CODE = "FORBIDDEN_OPERATION";

    public ForbiddenOperationException(String operation) {
        super(
                ERROR_CODE,
                "You are not authorized to perform this operation.",
                String.format("Forbidden operation attempted: %s", operation)
        );
    }
}