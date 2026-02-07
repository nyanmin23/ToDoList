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
    public ChildTaskResponse createChildTask(Long parentTaskId, ChildTaskRequest childTaskRequest) {
        ParentTask parentTask = parentTaskRepository.findById(parentTaskId)
                .orElseThrow(() -> new EntityNotFoundException("Parent task not found"));

        ChildTask createdChildTask = mapper.toEntity(childTaskRequest);
        createdChildTask.setParentTask(parentTask);

        return mapper.toResponse(childTaskRepository.save(createdChildTask));
    }

    @Transactional(readOnly = true)
    public List<ChildTaskResponse> getAllChildTasksByParent(Long parentTaskId) {
        if (!parentTaskRepository.existsByParentTaskId(parentTaskId)) {
            throw new EntityNotFoundException("Parent task not found");
        }

        return childTaskRepository.findByParentTask_ParentTaskIdOrderByDisplayOrder(parentTaskId)
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ChildTaskResponse updateChildTask(Long childTaskId, ChildTaskRequest childTaskRequest) {
        ChildTask updatedChildTask = childTaskRepository.findById(childTaskId)
                .orElseThrow(() -> new EntityNotFoundException("Child task not found"));

        mapper.updateEntityFromRequest(childTaskRequest, updatedChildTask);

        return mapper.toResponse(childTaskRepository.save(updatedChildTask));
    }

    @Transactional
    public void deleteChildTask(Long childTaskId) {
        ChildTask childTask = childTaskRepository.findById(childTaskId)
                .orElseThrow(() -> new EntityNotFoundException("Child task not found"));

        childTaskRepository.delete(childTask);
    }
}
