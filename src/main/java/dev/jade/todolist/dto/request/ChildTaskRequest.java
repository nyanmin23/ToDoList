package dev.jade.todolist.dto.request;

import dev.jade.todolist.models.Priority;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class ChildTaskRequest {

    @NotNull(message = "Description is required")
    @NotEmpty(message = "Description cannot be empty")
    private String childTaskTitle;

    private Instant deadline;

    private Priority priority;

    private boolean isCompleted;

    @NotNull
    private Integer displayOrder;

}