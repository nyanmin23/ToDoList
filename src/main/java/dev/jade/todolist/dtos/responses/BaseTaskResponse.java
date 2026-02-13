package dev.jade.todolist.dtos.responses;

import dev.jade.todolist.models.Priority;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public abstract class BaseTaskResponse {

    private Instant deadline;

    private Priority priority;

    private boolean isCompleted;

    private Instant createdAt;

    private Instant updatedAt;

    private Instant completedAt;

}
