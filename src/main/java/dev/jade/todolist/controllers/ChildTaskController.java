package dev.jade.todolist.controllers;

import dev.jade.todolist.dto.request.ChildTaskRequest;
import dev.jade.todolist.dto.response.ChildTaskResponse;
import dev.jade.todolist.services.ChildTaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/{userId}/sections/{sectionId}/parent-tasks/{parentTaskId}/child-tasks")
public class ChildTaskController {

    private final ChildTaskService childTaskService;

    @PostMapping
    public ResponseEntity<ChildTaskResponse> addNewChildTask(
            @PathVariable Long parentTaskId,
            @Valid @RequestBody ChildTaskRequest childTaskRequest
    ) {
        ChildTaskResponse newChildTask = childTaskService.createChildTask(parentTaskId, childTaskRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(newChildTask);
    }

    @GetMapping
    public ResponseEntity<List<ChildTaskResponse>> displayAllChildTasksByParent(
            @PathVariable Long parentTaskId
    ) {
        List<ChildTaskResponse> allChildTasks = childTaskService.getAllChildTasksByParent(parentTaskId);

        return ResponseEntity.ok(allChildTasks);
    }

    @PutMapping("/{childTaskId}")
    public ResponseEntity<ChildTaskResponse> updateChildTask(
            @PathVariable Long childTaskId,
            @Valid @RequestBody ChildTaskRequest childTaskRequest
    ) {
        ChildTaskResponse updatedChildTask = childTaskService.updateChildTask(childTaskId, childTaskRequest);

        return ResponseEntity.ok(updatedChildTask);
    }

    @DeleteMapping("/{childTaskId}")
    public ResponseEntity<ChildTaskResponse> deleteChildTask(
            @PathVariable Long childTaskId
    ) {
        ChildTaskResponse deletedChildTask = childTaskService.deleteChildTask(childTaskId);

        return ResponseEntity.ok(deletedChildTask);
    }
}
