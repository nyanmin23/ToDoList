package dev.jade.todolist.common.exception;

/**
 * Base exception for infrastructure-level failures.
 * HTTP Status: 500 INTERNAL SERVER ERROR (unless more specific)
 */
public abstract class InfrastructureException extends RuntimeException {

    private final String errorCode;
    private final String userMessage;
    private final String developerMessage;

    protected InfrastructureException(
            String errorCode,
            String userMessage,
            String developerMessage
    ) {
        super(developerMessage);
        this.errorCode = errorCode;
        this.userMessage = userMessage;
        this.developerMessage = developerMessage;
    }

    protected InfrastructureException(
            String errorCode,
            String userMessage,
            String developerMessage,
            Throwable cause
    ) {
        super(developerMessage, cause);
        this.errorCode = errorCode;
        this.userMessage = userMessage;
        this.developerMessage = developerMessage;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getUserMessage() {
        return userMessage;
    }

    public String getDeveloperMessage() {
        return developerMessage;
    }
}

/**
 * Base exception for database/persistence layer failures.
 * HTTP Status: 500 INTERNAL SERVER ERROR
 */
abstract class PersistenceException extends InfrastructureException {

    protected PersistenceException(
            String errorCode,
            String userMessage,
            String developerMessage,
            Throwable cause
    ) {
        super(errorCode, userMessage, developerMessage, cause);
    }
}

/**
 * Exception thrown when database operations fail.
 * HTTP Status: 500 INTERNAL SERVER ERROR
 */
class DataAccessFailureException extends PersistenceException {

    private static final String ERROR_CODE = "DATA_ACCESS_FAILURE";

    public DataAccessFailureException(String operation, Throwable cause) {
        super(
                ERROR_CODE,
                "A database error occurred. Please try again later.",
                String.format("Data access failed during operation: %s", operation),
                cause
        );
    }
}

/**
 * Base exception for external service failures.
 * HTTP Status: 502 BAD GATEWAY or 503 SERVICE UNAVAILABLE
 */
abstract class ExternalServiceException extends InfrastructureException {

    protected ExternalServiceException(
            String errorCode,
            String userMessage,
            String developerMessage,
            Throwable cause
    ) {
        super(errorCode, userMessage, developerMessage, cause);
    }
}