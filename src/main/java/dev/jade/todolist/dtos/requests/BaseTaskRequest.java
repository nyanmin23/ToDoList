package dev.jade.todolist.dtos.requests;

import dev.jade.todolist.models.Priority;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.Instant;

@Getter
public abstract class BaseTaskRequest {

    @Future(message = "Deadline must be in the future")
    private Instant deadline;

    private Priority priority;

    private boolean isCompleted;

    @NotNull(message = "Display order is required")
    @Min(value = 1, message = "Display order must be non-negative")
    private Integer displayOrder;

}
