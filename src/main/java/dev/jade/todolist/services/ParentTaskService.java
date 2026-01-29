package dev.jade.todolist.services;

import dev.jade.todolist.dto.ParentTaskDTO;
import dev.jade.todolist.exceptions.EntityNotFoundException;
import dev.jade.todolist.models.ParentTask;
import dev.jade.todolist.models.Section;
import dev.jade.todolist.repositories.ParentTaskRepository;
import dev.jade.todolist.repositories.SectionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ParentTaskService {

    private final ParentTaskRepository parentTaskRepository;
    private final SectionRepository sectionRepository;

    public ParentTaskDTO createParentTask(
            Long sectionId,
            ParentTaskDTO parentTaskDTO
    ) {
        Section section = sectionRepository.findBySectionId(sectionId)
                .orElseThrow(
                        () -> new EntityNotFoundException("Section with id " + sectionId + " not found")
                );

        ParentTask parentTask = new ParentTask();
        parentTask.setDescription(parentTaskDTO.getDescription());
        parentTask.setDeadline(parentTaskDTO.getDeadline());
        parentTask.setPriority(parentTaskDTO.getPriority());
        parentTask.setCompleted(parentTaskDTO.isCompleted());
        parentTask.setSection(section);

        return mapToParentTaskDTO(parentTaskRepository.save(parentTask));
    }

    private ParentTaskDTO mapToParentTaskDTO(ParentTask parentTask) {
        ParentTaskDTO parentTaskDTO = new ParentTaskDTO();
        parentTaskDTO.setDescription(parentTask.getDescription());
        parentTaskDTO.setDeadline(parentTask.getDeadline());
        parentTaskDTO.setPriority(parentTask.getPriority());
        parentTaskDTO.setCompleted(parentTask.isCompleted());

        return parentTaskDTO;
    }

    public ParentTaskDTO updateParentTask(
            Long parentId,
            ParentTaskDTO parentTaskDTO
    ) {
        ParentTask parentTask = parentTaskRepository.findById(parentId)
                .orElseThrow(
                        () -> new EntityNotFoundException("Parent task with id " + parentId + " not found")
                );

        parentTask.setDescription(parentTaskDTO.getDescription());
        parentTask.setDeadline(parentTaskDTO.getDeadline());
        parentTask.setPriority(parentTaskDTO.getPriority());
        parentTask.setCompleted(parentTaskDTO.isCompleted());

        return mapToParentTaskDTO(parentTaskRepository.save(parentTask));
    }

}
