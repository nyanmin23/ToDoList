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
    public ParentTaskResponse createParentTask(
            Long userId,
            Long sectionId,
            ParentTaskRequest request) {

        Section section = sectionRepository
                .findByIdAndUserId(sectionId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Section", "id", sectionId));

        ParentTask createdParentTask = mapper.toEntity(request);
        createdParentTask.setSection(section);
        return mapper.toResponse(parentTaskRepository.save(createdParentTask));
    }

    @Transactional(readOnly = true)
    public List<ParentTaskResponse> findParentTasksBySection(
            Long userId,
            Long sectionId) {
        if (!sectionRepository.existsByIdAndUserId(sectionId, userId))
            throw new EntityNotFoundException("Section", "id", sectionId);

        return parentTaskRepository
                .findBySection_SectionIdOrderByCreatedAt(sectionId)
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ParentTaskResponse updateParentTask(
            Long userId,
            Long sectionId,
            Long parentTaskId,
            ParentTaskRequest request) {

        ParentTask updatedParentTask = parentTaskRepository
                .findByIdAndUserId(parentTaskId, userId)
                .orElseThrow(() -> new EntityNotFoundException("ParentTask", "id", parentTaskId));

        mapper.updateEntityFromRequest(request, updatedParentTask);
        return mapper.toResponse(parentTaskRepository.save(updatedParentTask));
    }

    @Transactional
    public void deleteParentTask(
            Long userId,
            Long parentTaskId) {

        ParentTask deletedParentTask = parentTaskRepository
                .findByIdAndUserId(parentTaskId, userId)
                .orElseThrow(() -> new EntityNotFoundException("ParentTask", "id", parentTaskId));

        parentTaskRepository.delete(deletedParentTask);
    }
}
