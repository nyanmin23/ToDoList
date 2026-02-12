package dev.jade.todolist.common.exception;

/**
 * Base exception for domain/business logic violations.
 * Represents errors related to business rules, resource states, and domain constraints.
 */
public abstract class DomainException extends ToDoListApplicationException {

    protected DomainException(
            String errorCode,
            String userMessage,
            String developerMessage
    ) {
        super(errorCode, userMessage, developerMessage);
    }

    protected DomainException(
            String errorCode,
            String userMessage,
            String developerMessage,
            Throwable cause
    ) {
        super(errorCode, userMessage, developerMessage, cause);
    }
}

