package dev.jade.todolist.services;

import dev.jade.todolist.dtos.requests.ParentTaskRequest;
import dev.jade.todolist.dtos.responses.ParentTaskResponse;
import dev.jade.todolist.exceptions.EntityNotFoundException;
import dev.jade.todolist.mapstruct.mappers.ParentTaskMapper;
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
    private final ParentTaskMapper mapper;

    @Transactional
    public ParentTaskResponse createParentTask(Long sectionId, ParentTaskRequest parentTaskRequest) {
        Section section = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new EntityNotFoundException("Section not found"));

        ParentTask createdParentTask = mapper.toEntity(parentTaskRequest);
        createdParentTask.setSection(section);

        return mapper.toResponse(parentTaskRepository.save(createdParentTask));
    }

    @Transactional(readOnly = true)
    public List<ParentTaskResponse> getAllParentTasksBySection(Long sectionId) {
        if (!sectionRepository.existsBySectionId(sectionId)) {
            throw new EntityNotFoundException("Section not found");
        }

        return parentTaskRepository.findBySection_SectionIdOrderByDisplayOrder(sectionId)
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ParentTaskResponse updateParentTask(Long parentTaskId, ParentTaskRequest parentTaskRequest) {
        ParentTask updatedParentTask = parentTaskRepository.findById(parentTaskId)
                .orElseThrow(() -> new EntityNotFoundException("Parent Task not found"));

        mapper.updateEntityFromRequest(parentTaskRequest, updatedParentTask);

        return mapper.toResponse(parentTaskRepository.save(updatedParentTask));
    }

    @Transactional
    public void deleteParentTask(Long parentTaskId) {
        ParentTask parentTask = parentTaskRepository.findById(parentTaskId)
                .orElseThrow(() -> new EntityNotFoundException("Parent Task not found"));

        parentTaskRepository.delete(parentTask);
    }
}
