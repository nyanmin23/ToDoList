package dev.jade.todolist.task.parent.dto.response;

import dev.jade.todolist.models.dtos.responses.BaseTaskResponse;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
class ParentTaskResponse extends BaseTaskResponse {

    private Long parentTaskId;
    private String parentTaskTitle;
}