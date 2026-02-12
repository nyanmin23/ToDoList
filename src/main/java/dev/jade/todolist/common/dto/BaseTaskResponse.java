package dev.jade.todolist.common.dto;

import dev.jade.todolist.domain.enums.Priority;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
abstract class BaseTaskResponse {

    private Instant deadline;
    private Priority priority;
    private boolean isCompleted;
    private String rank;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant completedAt;
}
