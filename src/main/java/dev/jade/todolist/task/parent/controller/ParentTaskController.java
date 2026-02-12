package dev.jade.todolist.task.parent.controller;

import dev.jade.todolist.models.dtos.requests.ParentTaskRequest;
import dev.jade.todolist.models.dtos.responses.ParentTaskResponse;
import dev.jade.todolist.security.CustomUserDetails;
import dev.jade.todolist.task.parent.service.ParentTaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sections/{sectionId}/parent-tasks")
public class ParentTaskController {

    private final ParentTaskService parentTaskService;

    @PostMapping
    public ResponseEntity<ParentTaskResponse> createParentTask(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable Long sectionId,
            @Valid @RequestBody ParentTaskRequest request
    ) {
        Long userId = currentUser.getUserId();
        ParentTaskResponse task = parentTaskService.createParentTask(userId, sectionId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(task);
    }

    @GetMapping
    public ResponseEntity<List<ParentTaskResponse>> getParentTasks(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable Long sectionId
    ) {
        Long userId = currentUser.getUserId();
        List<ParentTaskResponse> tasks = parentTaskService.findParentTasksBySection(userId, sectionId);
        return ResponseEntity.ok(tasks);
    }

    @PutMapping("/{parentTaskId}")
    public ResponseEntity<ParentTaskResponse> updateParentTask(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable Long sectionId,
            @PathVariable Long parentTaskId,
            @Valid @RequestBody ParentTaskRequest request
    ) {
        Long userId = currentUser.getUserId();
        ParentTaskResponse updated = parentTaskService.updateParentTask(userId, sectionId, parentTaskId, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{parentTaskId}")
    public ResponseEntity<Void> deleteParentTask(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable Long sectionId,
            @PathVariable Long parentTaskId
    ) {
        Long userId = currentUser.getUserId();
        parentTaskService.deleteParentTask(userId, parentTaskId);
        return ResponseEntity.noContent().build();
    }
}