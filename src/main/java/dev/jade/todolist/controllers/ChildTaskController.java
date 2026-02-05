package dev.jade.todolist.controllers;

import dev.jade.todolist.services.ChildTaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/{userId}/sections/{sectionId}/parent-tasks/{parentId}/child-tasks")
public class ChildTaskController {

    private final ChildTaskService childTaskService;

}
