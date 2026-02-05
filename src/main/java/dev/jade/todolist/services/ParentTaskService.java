package dev.jade.todolist.services;

import dev.jade.todolist.dto.request.ParentTaskRequest;
import dev.jade.todolist.dto.response.ParentTaskResponse;
import dev.jade.todolist.exceptions.EntityNotFoundException;
import dev.jade.todolist.models.ParentTask;
import dev.jade.todolist.models.Section;
import dev.jade.todolist.repositories.ParentTaskRepository;
import dev.jade.todolist.repositories.SectionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ParentTaskService {

    private final ParentTaskRepository parentTaskRepository;
    private final SectionRepository sectionRepository;

    @Transactional
    public ParentTaskResponse createParentTask(Long sectionId, ParentTaskRequest parentTaskRequest) {
        Section section = sectionRepository.findBySectionId(sectionId)
                .orElseThrow(() -> new EntityNotFoundException("Section not found"));

        ParentTask createdParentTask = new ParentTask();
        createdParentTask.setParentTaskTitle(parentTaskRequest.getParentTaskTitle());
        createdParentTask.setDeadline(parentTaskRequest.getDeadline());
        createdParentTask.setPriority(parentTaskRequest.getPriority());
        createdParentTask.setCompleted(parentTaskRequest.isCompleted());
        createdParentTask.setDisplayOrder(parentTaskRequest.getDisplayOrder());
        createdParentTask.setSection(section);

        return toParentTaskResponse(parentTaskRepository.save(createdParentTask));
    }

    @Transactional(readOnly = true)
    public List<ParentTaskResponse> getAllParentTasksBySection(Long sectionId) {
        if (!sectionRepository.existsBySectionId(sectionId)) {
            throw new EntityNotFoundException("Section not found");
        }

        return parentTaskRepository.findAllBySection_SectionId(sectionId)
                .stream()
                .map(this::toParentTaskResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ParentTaskResponse updateParentTask(Long sectionId, ParentTaskRequest parentTaskRequest) {
        ParentTask updatedParentTask = parentTaskRepository.findById(sectionId)
                .orElseThrow(() -> new EntityNotFoundException("Parent Task not found"));

        updatedParentTask.setParentTaskTitle(parentTaskRequest.getParentTaskTitle());
        updatedParentTask.setDeadline(parentTaskRequest.getDeadline());
        updatedParentTask.setPriority(parentTaskRequest.getPriority());
        updatedParentTask.setCompleted(parentTaskRequest.isCompleted());
        updatedParentTask.setDisplayOrder(parentTaskRequest.getDisplayOrder());

        return toParentTaskResponse(parentTaskRepository.save(updatedParentTask));
    }

    @Transactional
    public ParentTaskResponse deleteParentTask(Long parentTaskId) {
        ParentTask parentTask = parentTaskRepository.findById(parentTaskId)
                .orElseThrow(() -> new EntityNotFoundException("Parent Task not found"));

        ParentTaskResponse parentTaskResponse = toParentTaskResponse(parentTask);
        parentTaskRepository.delete(parentTask);

        return parentTaskResponse;
    }


    private ParentTaskResponse toParentTaskResponse(ParentTask parentTask) {
        ParentTaskResponse parentTaskResponse = new ParentTaskResponse();

        parentTaskResponse.setParentTaskId(parentTask.getParentTaskId());
        parentTaskResponse.setParentTaskTitle(parentTask.getParentTaskTitle());
        parentTaskResponse.setDeadline(parentTask.getDeadline());
        parentTaskResponse.setPriority(parentTask.getPriority());
        parentTaskResponse.setCompleted(parentTask.isCompleted());
        parentTaskResponse.setDisplayOrder(parentTask.getDisplayOrder());

        return parentTaskResponse;
    }
}
