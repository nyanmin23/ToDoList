package dev.jade.todolist.task.child.controller;

import dev.jade.todolist.models.dtos.requests.ChildTaskRequest;
import dev.jade.todolist.security.CustomUserDetails;
import dev.jade.todolist.task.child.dto.response.ChildTaskResponse;
import dev.jade.todolist.task.child.service.ChildTaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sections/{sectionId}/parent-tasks/{parentTaskId}/child-tasks")
public class ChildTaskController {

    private final ChildTaskService childTaskService;

    @PostMapping
    public ResponseEntity<ChildTaskResponse> createChildTask(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable Long sectionId,
            @PathVariable Long parentTaskId,
            @Valid @RequestBody ChildTaskRequest request
    ) {
        Long userId = currentUser.getUserId();
        ChildTaskResponse response = childTaskService.createChildTask(userId, sectionId, parentTaskId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<ChildTaskResponse>> getChildTasks(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable Long sectionId,
            @PathVariable Long parentTaskId
    ) {
        Long userId = currentUser.getUserId();
        List<ChildTaskResponse> tasks = childTaskService.findChildTasksByParent(userId, parentTaskId);
        return ResponseEntity.ok(tasks);
    }

    @PutMapping("/{childTaskId}")
    public ResponseEntity<ChildTaskResponse> updateChildTask(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable Long sectionId,
            @PathVariable Long parentTaskId,
            @PathVariable Long childTaskId,
            @Valid @RequestBody ChildTaskRequest request
    ) {
        Long userId = currentUser.getUserId();
        ChildTaskResponse updated = childTaskService.updateChildTask(userId, sectionId, parentTaskId, childTaskId, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{childTaskId}")
    public ResponseEntity<Void> deleteChildTask(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable Long sectionId,
            @PathVariable Long parentTaskId,
            @PathVariable Long childTaskId
    ) {
        Long userId = currentUser.getUserId();
        childTaskService.deleteChildTask(userId, childTaskId);
        return ResponseEntity.noContent().build();
    }
}
