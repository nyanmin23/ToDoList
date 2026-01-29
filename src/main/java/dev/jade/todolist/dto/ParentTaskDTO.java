package dev.jade.todolist.dto;

import dev.jade.todolist.models.Priority;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class ParentTaskDTO {

    @NotNull
    @NotEmpty
    private String description;

    private Instant deadline;

    private Priority priority;

    @NotNull
    @NotEmpty
    private boolean isCompleted;

}
