package dev.jade.todolist.controllers;

import dev.jade.todolist.dto.ChildTaskDTO;
import dev.jade.todolist.services.ChildTaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sections/{sectionId}/parent-tasks/{parentId}/child-tasks")
public class ChildTaskController {

    private final ChildTaskService childTaskService;

    @PostMapping
    public ResponseEntity<ChildTaskDTO> addChildTask(
            @PathVariable Long sectionId,
            @PathVariable Long parentId,
            @RequestBody ChildTaskDTO childTaskDTO
    ) {
        ChildTaskDTO createdChildTask = childTaskService.createChildTask(parentId, childTaskDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdChildTask);
    }

    @GetMapping
    public ResponseEntity<List<ChildTaskDTO>> getAllChildTasksByParent(
            @PathVariable Long sectionId,
            @PathVariable Long parentId
    ) {
        List<ChildTaskDTO> childTasks = childTaskService.getAllChildTasksByParent(parentId);

        return ResponseEntity.ok(childTasks);
    }

    @GetMapping("/{childId}")
    public ResponseEntity<ChildTaskDTO> getChildTaskById(
            @PathVariable Long sectionId,
            @PathVariable Long parentId,
            @PathVariable Long childId
    ) {
        ChildTaskDTO childTask = childTaskService.getChildTaskById(childId);

        return ResponseEntity.ok(childTask);
    }

    @PutMapping("/{childId}")
    public ResponseEntity<ChildTaskDTO> updateChildTask(
            @PathVariable Long sectionId,
            @PathVariable Long parentId,
            @PathVariable Long childId,
            @RequestBody ChildTaskDTO childTaskDTO
    ) {
        ChildTaskDTO updatedChildTask = childTaskService.updateChildTask(childId, childTaskDTO);

        return ResponseEntity.ok(updatedChildTask);
    }
}
