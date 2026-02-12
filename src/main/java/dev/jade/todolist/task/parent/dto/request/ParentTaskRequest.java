package dev.jade.todolist.task.parent.dto.request;

import dev.jade.todolist.models.dtos.requests.BaseTaskRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
class ParentTaskRequest extends BaseTaskRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title cannot exceed 255 characters")
    private String parentTaskTitle;
}