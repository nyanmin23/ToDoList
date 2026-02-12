package dev.jade.todolist.common.exception;

import lombok.Getter;

/**
 * Base exception for all application-level exceptions.
 * Provides structured error information for consistent error handling.
 */
@Getter
public abstract class ToDoListApplicationException extends RuntimeException {

    private final String errorCode;
    private final String userMessage;
    private final String developerMessage;

    protected ToDoListApplicationException(
            String errorCode,
            String userMessage,
            String developerMessage
    ) {
        super(developerMessage);
        this.errorCode = errorCode;
        this.userMessage = userMessage;
        this.developerMessage = developerMessage;
    }

    protected ToDoListApplicationException(
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
}