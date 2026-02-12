package dev.jade.todolist.common.exception;

/**
 * Base exception for resource not found errors.
 * HTTP Status: 404 NOT FOUND
 */
public abstract class ResourceNotFoundException extends DomainException {

    protected ResourceNotFoundException(
            String errorCode,
            String userMessage,
            String developerMessage
    ) {
        super(errorCode, userMessage, developerMessage);
    }

    /**
     * Standardized factory method for resource not found by ID
     */
    protected static String buildUserMessage(String resourceType) {
        return String.format("The requested %s could not be found.", resourceType.toLowerCase());
    }

    protected static String buildDeveloperMessage(
            String resourceType,
            String fieldName,
            Object fieldValue
    ) {
        return String.format(
                "%s not found with %s: %s",
                resourceType,
                fieldName,
                fieldValue
        );
    }
}