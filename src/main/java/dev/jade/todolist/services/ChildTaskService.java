package dev.jade.todolist.services;

import dev.jade.todolist.dtos.requests.ChildTaskRequest;
import dev.jade.todolist.dtos.responses.ChildTaskResponse;
import dev.jade.todolist.exceptions.EntityNotFoundException;
import dev.jade.todolist.mapstruct.mappers.ChildTaskMapper;
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
    private final ChildTaskMapper mapper;

    @Transactional
    public ChildTaskResponse createChildTask(
            Long userId,
            Long sectionId,
            Long parentTaskId,
            ChildTaskRequest request) {

        ParentTask parentTask = parentTaskRepository
                .findByIdAndUserId(parentTaskId, userId)
                .orElseThrow(() -> new EntityNotFoundException("ParentTask", "id", parentTaskId));

        ChildTask createdChildTask = mapper.toEntity(request);
        createdChildTask.setParentTask(parentTask);
        return mapper.toResponse(childTaskRepository.save(createdChildTask));
    }

    @Transactional(readOnly = true)
    public List<ChildTaskResponse> findChildTasksByParent(
            Long userId,
            Long parentTaskId) {

        if (!parentTaskRepository.existsByIdAndUserId(parentTaskId, userId))
            throw new EntityNotFoundException("ParentTask", "id", parentTaskId);

        return childTaskRepository
                .findByParentTask_ParentTaskIdOrderByCreatedAt(parentTaskId)
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
                .orElseThrow(() -> new EntityNotFoundException("ChildTask", "id", childTaskId));

        mapper.updateEntityFromRequest(request, updatedChildTask);
        return mapper.toResponse(childTaskRepository.save(updatedChildTask));
    }

    @Transactional
    public void deleteChildTask(Long userId, Long childTaskId) {
        ChildTask task = childTaskRepository
                .findByIdAndUserId(childTaskId, userId)
                .orElseThrow(() -> new EntityNotFoundException("ChildTask", "id", childTaskId));

        childTaskRepository.delete(task);
    }
}
