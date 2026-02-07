package dev.jade.todolist.dtos.requests;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import dev.jade.todolist.models.Priority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class ParentTaskRequest {

    @NotBlank(message = "Title is required")
    private String parentTaskTitle;

    private Instant deadline;

    @JsonSetter(nulls = Nulls.SKIP)
    private Priority priority = Priority.LOW;

    private boolean isCompleted;

    @NotNull(message = "Display order is required")
    private Integer displayOrder;

}