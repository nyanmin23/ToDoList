package dev.jade.todolist.common.exception;

/**
 * Base exception for business rule violations.
 * HTTP Status: 409 CONFLICT or 422 UNPROCESSABLE ENTITY
 */
public abstract class BusinessRuleViolationException extends DomainException {

    protected BusinessRuleViolationException(
            String errorCode,
            String userMessage,
            String developerMessage
    ) {
        super(errorCode, userMessage, developerMessage);
    }
}

/**
 * Exception thrown when attempting to create a user with an email that already exists.
 * HTTP Status: 409 CONFLICT
 */
public class UserAlreadyExistsException extends BusinessRuleViolationException {

    private static final String ERROR_CODE = "USER_ALREADY_EXISTS";

    public UserAlreadyExistsException(String email) {
        super(
                ERROR_CODE,
                "An account with this email already exists.",
                String.format("User registration failed: email '%s' already exists", email)
        );
    }
}

/**
 * Exception thrown when task state transition violates business rules.
 * HTTP Status: 422 UNPROCESSABLE ENTITY
 */
class InvalidTaskStateException extends BusinessRuleViolationException {

    private static final String ERROR_CODE = "INVALID_TASK_STATE";

    public InvalidTaskStateException(String reason) {
        super(
                ERROR_CODE,
                "This operation cannot be performed on the task in its current state.",
                reason
        );
    }
}

/**
 * Exception thrown when attempting to create duplicate display order values.
 * HTTP Status: 409 CONFLICT
 */
class DuplicateDisplayOrderException extends BusinessRuleViolationException {

    private static final String ERROR_CODE = "DUPLICATE_DISPLAY_ORDER";

    public DuplicateDisplayOrderException(String rank, String context) {
        super(
                ERROR_CODE,
                "A conflict occurred while ordering items. Please try again.",
                String.format("Duplicate rank '%s' detected in context: %s", rank, context)
        );
    }
}