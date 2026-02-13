package dev.jade.todolist.dtos.requests;

import dev.jade.todolist.models.Priority;
import jakarta.validation.constraints.Future;
import lombok.Getter;

import java.time.Instant;

@Getter
public abstract class BaseTaskRequest {

    @Future(message = "Deadline must be in the future")
    private Instant deadline;

    private Priority priority;

    private boolean isCompleted;

}
