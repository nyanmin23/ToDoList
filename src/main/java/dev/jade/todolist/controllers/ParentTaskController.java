package dev.jade.todolist.controllers;

import dev.jade.todolist.dto.request.ParentTaskRequest;
import dev.jade.todolist.dto.response.ParentTaskResponse;
import dev.jade.todolist.services.ParentTaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/{userId}/sections/{sectionId}/parent-tasks")
public class ParentTaskController {

    private final ParentTaskService parentTaskService;

    @PostMapping
    public ResponseEntity<ParentTaskResponse> addNewParentTask(
            @PathVariable Long sectionId,
            @Valid @RequestBody ParentTaskRequest parentTaskRequest
    ) {
        ParentTaskResponse newParentTask = parentTaskService.createParentTask(sectionId, parentTaskRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(newParentTask);
    }

    @GetMapping
    public ResponseEntity<List<ParentTaskResponse>> displayAllParentTasksByUser(
            @PathVariable Long sectionId
    ) {
        List<ParentTaskResponse> allParentTasks = parentTaskService.getAllParentTasksBySection(sectionId);

        return ResponseEntity.ok(allParentTasks);
    }

    @PutMapping("/{parentTaskId}")
    public ResponseEntity<ParentTaskResponse> updateParentTask(
            @PathVariable Long parentTaskId,
            @Valid @RequestBody ParentTaskRequest parentTaskRequest
    ) {
        ParentTaskResponse updatedParentTask = parentTaskService.updateParentTask(parentTaskId, parentTaskRequest);

        return ResponseEntity.ok(updatedParentTask);
    }

    @DeleteMapping("/{parentTaskId}")
    public ResponseEntity<ParentTaskResponse> deleteSection(
            @PathVariable Long parentTaskId
    ) {
        ParentTaskResponse deletedParentTask = parentTaskService.deleteParentTask(parentTaskId);

        return ResponseEntity.ok(deletedParentTask);
    }

}
