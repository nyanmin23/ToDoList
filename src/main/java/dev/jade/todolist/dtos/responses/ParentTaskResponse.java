package dev.jade.todolist.dtos.responses;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ParentTaskResponse extends BaseTaskResponse {

    private Long parentTaskId;

    private String parentTaskTitle;

}