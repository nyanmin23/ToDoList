package dev.jade.todolist.services;

import dev.jade.todolist.dto.ChildTaskDTO;
import dev.jade.todolist.exceptions.EntityNotFoundException;
import dev.jade.todolist.models.ChildTask;
import dev.jade.todolist.models.ParentTask;
import dev.jade.todolist.repositories.ChildTaskRepository;
import dev.jade.todolist.repositories.ParentTaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChildTaskService {

    private final ChildTaskRepository childTaskRepository;
    private final ParentTaskRepository parentTaskRepository;

    public ChildTaskDTO createChildTask(
            Long parentId,
            ChildTaskDTO childTaskDTO
    ) {

        ParentTask parentTask = parentTaskRepository.findById(parentId)
                .orElseThrow(
                        () -> new EntityNotFoundException("Parent task with id " + parentId + " not found")
                );

        ChildTask childTask = new ChildTask();
        childTask.setDescription(childTaskDTO.getDescription());
        childTask.setDeadline(childTaskDTO.getDeadline());
        childTask.setPriority(childTaskDTO.getPriority());
        childTask.setCompleted(childTaskDTO.isCompleted());
        childTask.setParentTask(parentTask);

        return mapToChildTaskDTO(childTaskRepository.save(childTask));
    }

    public List<ChildTaskDTO> getAllChildTasksByParent(Long parentId) {
        parentTaskRepository.findById(parentId)
                .orElseThrow(
                        () -> new EntityNotFoundException("Parent task with id " + parentId + " not found")
                );

        return childTaskRepository.findAllByParentTask_ParentId(parentId)
                .stream()
                .map(this::mapToChildTaskDTO)
                .collect(Collectors.toList());
    }

    public ChildTaskDTO getChildTaskById(Long childId) {
        ChildTask childTask = childTaskRepository.findById(childId)
                .orElseThrow(
                        () -> new EntityNotFoundException("Child task with id " + childId + " not found")
                );

        return mapToChildTaskDTO(childTask);
    }

    private ChildTaskDTO mapToChildTaskDTO(ChildTask childTask) {
        ChildTaskDTO childTaskDTO = new ChildTaskDTO();
        childTaskDTO.setChildId(childTask.getChildId());
        childTaskDTO.setDescription(childTask.getDescription());
        childTaskDTO.setDeadline(childTask.getDeadline());
        childTaskDTO.setPriority(childTask.getPriority());
        childTaskDTO.setCompleted(childTask.isCompleted());

        return childTaskDTO;
    }

    public ChildTaskDTO updateChildTask(
            Long childId,
            ChildTaskDTO childTaskDTO
    ) {
        ChildTask childTask = childTaskRepository.findById(childId)
                .orElseThrow(
                        () -> new EntityNotFoundException("Child task with id " + childId + " not found")
                );
        childTask.setDescription(childTaskDTO.getDescription());
        childTask.setDeadline(childTaskDTO.getDeadline());
        childTask.setPriority(childTaskDTO.getPriority());
        childTask.setCompleted(childTaskDTO.isCompleted());

        return mapToChildTaskDTO(childTaskRepository.save(childTask));
    }

}
