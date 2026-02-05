package dev.jade.todolist.dto.response;

import dev.jade.todolist.models.Priority;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class ParentTaskResponse {

    private Long parentTaskId;
    private String parentTaskTitle;
    private Instant deadline;
    private Priority priority;
    private boolean isCompleted;
    private Integer displayOrder;

}