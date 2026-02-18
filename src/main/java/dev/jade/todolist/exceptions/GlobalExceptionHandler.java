package dev.jade.todolist.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        ApiErrorResponse body = buildError(
                HttpStatus.BAD_REQUEST,
                "VALIDATION_ERROR",
                "Validation failed",
                fieldErrors,
                request
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(
            EntityNotFoundException ex,
            HttpServletRequest request
    ) {
        ApiErrorResponse body = buildError(
                HttpStatus.NOT_FOUND,
                "NOT_FOUND",
                ex.getMessage(),
                null,
                request
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ApiErrorResponse> handleConflict(
            UserAlreadyExistsException ex,
            HttpServletRequest request
    ) {
        ApiErrorResponse body = buildError(
                HttpStatus.CONFLICT,
                "CONFLICT",
                ex.getMessage(),
                null,
                request
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler({InvalidCredentialsException.class, BadCredentialsException.class})
    public ResponseEntity<ApiErrorResponse> handleInvalidCredentials(
            RuntimeException ex,
            HttpServletRequest request
    ) {
        ApiErrorResponse body = buildError(
                HttpStatus.UNAUTHORIZED,
                "UNAUTHORIZED",
                "Invalid credentials",
                null,
                request
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    @ExceptionHandler({UnauthorizedAccessException.class, AccessDeniedException.class})
    public ResponseEntity<ApiErrorResponse> handleAccessDenied(
            RuntimeException ex,
            HttpServletRequest request
    ) {
        ApiErrorResponse body = buildError(
                HttpStatus.FORBIDDEN,
                "FORBIDDEN",
                ex.getMessage(),
                null,
                request
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }

    @ExceptionHandler({AuthenticationException.class})
    public ResponseEntity<ApiErrorResponse> handleAuthException(
            AuthenticationException ex,
            HttpServletRequest request
    ) {
        ApiErrorResponse body = buildError(
                HttpStatus.UNAUTHORIZED,
                "UNAUTHORIZED",
                "Authentication required",
                null,
                request
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgument(
            IllegalArgumentException ex,
            HttpServletRequest request
    ) {
        ApiErrorResponse body = buildError(
                HttpStatus.BAD_REQUEST,
                "BAD_REQUEST",
                ex.getMessage(),
                null,
                request
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnexpected(
            Exception ex,
            HttpServletRequest request
    ) {
        ApiErrorResponse body = buildError(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "INTERNAL_ERROR",
                "Unexpected server error",
                null,
                request
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    private ApiErrorResponse buildError(
            HttpStatus status,
            String code,
            String message,
            Object details,
            HttpServletRequest request
    ) {
        return new ApiErrorResponse(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                code,
                message,
                details,
                request.getRequestURI()
        );
    }
}
