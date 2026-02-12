package dev.jade.todolist.task.child.service;

import dev.jade.todolist.models.dtos.requests.ChildTaskRequest;
import dev.jade.todolist.repositories.ChildTaskRepository;
import dev.jade.todolist.repositories.ParentTaskRepository;
import dev.jade.todolist.task.child.dto.response.ChildTaskResponse;
import dev.jade.todolist.task.child.entity.ChildTask;
import dev.jade.todolist.task.child.mapper.ChildTaskMapper;
import dev.jade.todolist.task.parent.entity.ParentTask;
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
    private final ChildTaskMapper mapper;

    @Transactional
    public ChildTaskResponse createChildTask(
            Long userId,
            Long sectionId,
            Long parentTaskId,
            ChildTaskRequest request) {

        ParentTask parentTask = parentTaskRepository
                .findByIdAndUserId(parentTaskId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found"));

        ChildTask createdChildTask = mapper.toEntity(request);
        createdChildTask.setParentTask(parentTask);
        return mapper.toResponse(childTaskRepository.save(createdChildTask));
    }

    @Transactional(readOnly = true)
    public List<ChildTaskResponse> findChildTasksByParent(
            Long userId,
            Long parentTaskId) {

        if (!parentTaskRepository.existsByIdAndUserId(parentTaskId, userId))
            throw new EntityNotFoundException("Task not found");

        return childTaskRepository
                .findByParentTask_ParentTaskIdOrderByDisplayOrder(parentTaskId)
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ChildTaskResponse updateChildTask(
            Long userId,
            Long sectionId,
            Long parentTaskId,
            Long childTaskId,
            ChildTaskRequest request) {

        ChildTask updatedChildTask = childTaskRepository
                .findByIdAndParentIdAndUserId(childTaskId, parentTaskId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found"));

        mapper.updateEntityFromRequest(request, updatedChildTask);
        return mapper.toResponse(childTaskRepository.save(updatedChildTask));
    }

    @Transactional
    public void deleteChildTask(Long userId, Long childTaskId) {
        ChildTask task = childTaskRepository
                .findByIdAndUserId(childTaskId, userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Child task not found or access denied"));

        childTaskRepository.delete(task);
    }
}
