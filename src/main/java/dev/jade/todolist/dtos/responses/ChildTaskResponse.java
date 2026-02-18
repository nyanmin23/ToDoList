package dev.jade.todolist.dtos.responses;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChildTaskResponse extends BaseTaskResponse {

    private Long childTaskId;

    private String childTaskTitle;

}
