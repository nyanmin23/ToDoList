package dev.jade.todolist.controllers;

import dev.jade.todolist.dto.ChildTaskDTO;
import dev.jade.todolist.services.ChildTaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sections/{sectionId}/parent-tasks/{parentId}/child-tasks")
public class ChildTaskController {

    private final ChildTaskService childTaskService;

    @PostMapping
    public ResponseEntity<ChildTaskDTO> addChildTask(
            @PathVariable Long parentId,
            @RequestBody ChildTaskDTO childTaskDTO
    ) {
        ChildTaskDTO createdChildTask = childTaskService.createChildTask(parentId, childTaskDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdChildTask);
    }

    @PutMapping("/{childId}")
    public ResponseEntity<ChildTaskDTO> updateChildTask(
            @PathVariable Long childId,
            @RequestBody ChildTaskDTO childTaskDTO
    ) {
        ChildTaskDTO updatedChildTask = childTaskService.updateChildTask(childId, childTaskDTO);

        return ResponseEntity.ok(updatedChildTask);
    }
}
