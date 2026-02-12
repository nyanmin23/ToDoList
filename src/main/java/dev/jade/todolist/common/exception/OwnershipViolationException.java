package dev.jade.todolist.common.exception;

/**
 * Base exception for ownership/authorization violations within the domain.
 * HTTP Status: 403 FORBIDDEN
 */
public abstract class OwnershipViolationException extends DomainException {

    protected OwnershipViolationException(
            String errorCode,
            String userMessage,
            String developerMessage
    ) {
        super(errorCode, userMessage, developerMessage);
    }
}

/**
 * Exception thrown when a user attempts to access or modify a resource they don't own.
 * HTTP Status: 403 FORBIDDEN
 */
public class UnauthorizedAccessException extends OwnershipViolationException {

    private static final String ERROR_CODE = "UNAUTHORIZED_ACCESS";

    public UnauthorizedAccessException(String resourceType, Long resourceId, Long userId) {
        super(
                ERROR_CODE,
                "You do not have permission to access this resource.",
                String.format(
                        "User %d attempted unauthorized access to %s %d",
                        userId,
                        resourceType,
                        resourceId
                )
        );
    }

    public UnauthorizedAccessException(String message) {
        super(
                ERROR_CODE,
                "You do not have permission to perform this action.",
                message
        );
    }
}