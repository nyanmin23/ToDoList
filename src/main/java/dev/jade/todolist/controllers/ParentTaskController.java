package dev.jade.todolist.controllers;

import dev.jade.todolist.dto.ParentTaskDTO;
import dev.jade.todolist.services.ParentTaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sections/{sectionId}/parent-tasks")
public class ParentTaskController {

    private final ParentTaskService parentTaskService;

    @PostMapping
    public ResponseEntity<ParentTaskDTO> addParentTask(
            @PathVariable Long sectionId,
            @RequestBody ParentTaskDTO parentTaskDTO
    ) {
        ParentTaskDTO createdParentTask = parentTaskService.createParentTask(sectionId, parentTaskDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdParentTask);
    }

    @GetMapping
    public ResponseEntity<List<ParentTaskDTO>> getAllParentTasksBySection(
            @PathVariable Long sectionId
    ) {
        List<ParentTaskDTO> parentTasks = parentTaskService.getAllParentTasksBySection(sectionId);

        return ResponseEntity.ok(parentTasks);
    }

    @GetMapping("/{parentId}")
    public ResponseEntity<ParentTaskDTO> getParentTaskById(
            @PathVariable Long sectionId,
            @PathVariable Long parentId
    ) {
        ParentTaskDTO parentTask = parentTaskService.getParentTaskById(parentId);

        return ResponseEntity.ok(parentTask);
    }

    @PutMapping("/{parentId}")
    public ResponseEntity<ParentTaskDTO> updateParentTask(
            @PathVariable Long sectionId,
            @PathVariable Long parentId,
            @RequestBody ParentTaskDTO parentTaskDTO
    ) {
        ParentTaskDTO updatedParentTask = parentTaskService.updateParentTask(parentId, parentTaskDTO);

        return ResponseEntity.ok(updatedParentTask);
    }
}
