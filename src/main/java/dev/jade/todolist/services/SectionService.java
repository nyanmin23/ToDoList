package dev.jade.todolist.services;

import dev.jade.todolist.dtos.requests.SectionRequest;
import dev.jade.todolist.dtos.responses.SectionResponse;
import dev.jade.todolist.exceptions.EntityNotFoundException;
import dev.jade.todolist.mapstruct.mappers.SectionMapper;
import dev.jade.todolist.models.Section;
import dev.jade.todolist.models.User;
import dev.jade.todolist.repositories.SectionRepository;
import dev.jade.todolist.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SectionService {

    // TODO: implement .orElseThrow() for repository methods later

    private final SectionRepository sectionRepository;
    private final UserRepository userRepository;
    private final SectionMapper mapper;

    @Transactional
    public SectionResponse createSection(
            Long userId,
            SectionRequest request) {

        User user = userRepository
                .findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Section not found"));

        Section createdSection = mapper.toEntity(request);
        createdSection.setUser(user);
        return mapper.toResponse(sectionRepository.save(createdSection));
    }

    @Transactional(readOnly = true)
    public List<SectionResponse> findSectionsByUser(Long userId) {

        if (!userRepository.existsById(userId))
            throw new EntityNotFoundException("User not found");

        return sectionRepository
                .findByUser_UserIdOrderByCreatedAt(userId)
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public SectionResponse updateSection(
            Long userId,
            Long sectionId,
            SectionRequest request) {

        Section updatedSection = sectionRepository
                .findByIdAndUserId(sectionId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Section not found"));

        mapper.updateEntityFromRequest(request, updatedSection);
        return mapper.toResponse(sectionRepository.save(updatedSection));
    }

    @Transactional
    public void deleteSection(
            Long userId,
            Long sectionId) {

        Section deletedSection = sectionRepository
                .findByIdAndUserId(sectionId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Section not found"));

        sectionRepository.delete(deletedSection);
    }
}