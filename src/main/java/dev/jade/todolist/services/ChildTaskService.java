package dev.jade.todolist.services;

import dev.jade.todolist.dto.request.ChildTaskRequest;
import dev.jade.todolist.dto.response.ChildTaskResponse;
import dev.jade.todolist.exceptions.EntityNotFoundException;
import dev.jade.todolist.models.ChildTask;
import dev.jade.todolist.models.ParentTask;
import dev.jade.todolist.repositories.ChildTaskRepository;
import dev.jade.todolist.repositories.ParentTaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChildTaskService {

    private final ChildTaskRepository childTaskRepository;
    private final ParentTaskRepository parentTaskRepository;

    @Transactional
    public ChildTaskResponse createChildTask(Long parentTaskId, ChildTaskRequest childTaskRequest) {
        ParentTask parentTask = parentTaskRepository.findById(parentTaskId)
                .orElseThrow(() -> new EntityNotFoundException("Parent task not found"));

        ChildTask createdChildTask = new ChildTask();
        createdChildTask.setChildTaskTitle(childTaskRequest.getChildTaskTitle());
        createdChildTask.setDeadline(childTaskRequest.getDeadline());
        createdChildTask.setPriority(childTaskRequest.getPriority());
        createdChildTask.setCompleted(childTaskRequest.isCompleted());
        createdChildTask.setDisplayOrder(childTaskRequest.getDisplayOrder());
        createdChildTask.setParentTask(parentTask);

        return toChildTaskResponse(childTaskRepository.save(createdChildTask));
    }

    @Transactional(readOnly = true)
    public List<ChildTaskResponse> getAllChildTasksByParent(Long parentTaskId) {
        if (!parentTaskRepository.existsByParentTaskId(parentTaskId)) {
            throw new EntityNotFoundException("Parent task not found");
        }

        return childTaskRepository.findByParentTask_ParentTaskIdOrderByDisplayOrder(parentTaskId)
                .stream()
                .map(this::toChildTaskResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ChildTaskResponse updateChildTask(Long childTaskId, ChildTaskRequest childTaskRequest) {
        ChildTask updatedChildTask = childTaskRepository.findById(childTaskId)
                .orElseThrow(() -> new EntityNotFoundException("Child task not found"));

        updatedChildTask.setChildTaskTitle(childTaskRequest.getChildTaskTitle());
        updatedChildTask.setDeadline(childTaskRequest.getDeadline());
        updatedChildTask.setPriority(childTaskRequest.getPriority());
        updatedChildTask.setCompleted(childTaskRequest.isCompleted());
        updatedChildTask.setDisplayOrder(childTaskRequest.getDisplayOrder());

        return toChildTaskResponse(childTaskRepository.save(updatedChildTask));
    }

    @Transactional
    public ChildTaskResponse deleteChildTask(Long childTaskId) {
        ChildTask childTask = childTaskRepository.findById(childTaskId)
                .orElseThrow(() -> new EntityNotFoundException("Child task not found"));

        ChildTaskResponse childTaskResponse = toChildTaskResponse(childTask);
        childTaskRepository.delete(childTask);

        return childTaskResponse;
    }

    private ChildTaskResponse toChildTaskResponse(ChildTask childTask) {
        ChildTaskResponse childTaskResponse = new ChildTaskResponse();

        childTaskResponse.setChildTaskId(childTask.getChildTaskId());
        childTaskResponse.setChildTaskTitle(childTask.getChildTaskTitle());
        childTaskResponse.setDeadline(childTask.getDeadline());
        childTaskResponse.setPriority(childTask.getPriority());
        childTaskResponse.setCompleted(childTask.isCompleted());
        childTaskResponse.setDisplayOrder(childTask.getDisplayOrder());
        childTaskResponse.setCreatedAt(childTask.getCreatedAt());
        childTaskResponse.setUpdatedAt(childTask.getUpdatedAt());
        childTaskResponse.setCompletedAt(childTask.getCompletedAt());

        return childTaskResponse;
    }
}
