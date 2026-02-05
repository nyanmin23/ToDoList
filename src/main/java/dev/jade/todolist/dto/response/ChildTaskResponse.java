package dev.jade.todolist.dto.response;

import dev.jade.todolist.models.Priority;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class ChildTaskResponse {

    private Long childTaskId;
    private String childTaskTitle;
    private Instant deadline;
    private Priority priority;
    private boolean isCompleted;
    private Integer displayOrder;

}
