package dev.jade.todolist.common.dto;

import dev.jade.todolist.domain.enums.Priority;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.time.Instant;

@Getter
abstract class BaseTaskRequest {

    @Future(message = "Deadline must be in the future")
    private Instant deadline;

    private Priority priority;

    private boolean isCompleted;

    /**
     * LexoRank for ordering
     * If null, server will generate appropriate rank
     */
    @Size(max = 50, message = "Rank cannot exceed 50 characters")
    private String rank;

    /**
     * Optional: Insert position hint
     */
    private InsertPosition insertPosition;
}