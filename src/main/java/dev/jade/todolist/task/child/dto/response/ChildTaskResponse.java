package dev.jade.todolist.task.child.dto.response;

import dev.jade.todolist.models.dtos.responses.BaseTaskResponse;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChildTaskResponse extends BaseTaskResponse {

    private Long childTaskId;

    private String childTaskTitle;

}
