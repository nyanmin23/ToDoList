package dev.jade.todolist.common.exception;

import java.util.Map;

/**
 * Base exception for validation errors.
 * HTTP Status: 400 BAD REQUEST
 */
public abstract class ValidationException extends ToDoListApplicationException {

    protected ValidationException(
            String errorCode,
            String userMessage,
            String developerMessage
    ) {
        super(errorCode, userMessage, developerMessage);
    }
}

/**
 * Exception thrown when request data fails validation.
 * HTTP Status: 400 BAD REQUEST
 */
class InvalidRequestException extends ValidationException {

    private static final String ERROR_CODE = "INVALID_REQUEST";
    private final Map<String, String> fieldErrors;

    public InvalidRequestException(Map<String, String> fieldErrors) {
        super(
                ERROR_CODE,
                "The request contains invalid data.",
                String.format("Validation failed for fields: %s", fieldErrors.keySet())
        );
        this.fieldErrors = fieldErrors;
    }

    public InvalidRequestException(String field, String message) {
        super(
                ERROR_CODE,
                "The request contains invalid data.",
                String.format("Validation failed for field '%s': %s", field, message)
        );
        this.fieldErrors = Map.of(field, message);
    }

    public Map<String, String> getFieldErrors() {
        return fieldErrors;
    }
}