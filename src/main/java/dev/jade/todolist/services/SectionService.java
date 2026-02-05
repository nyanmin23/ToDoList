package dev.jade.todolist.services;

import dev.jade.todolist.dto.request.SectionRequest;
import dev.jade.todolist.dto.response.SectionResponse;
import dev.jade.todolist.exceptions.EntityNotFoundException;
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

    private final SectionRepository sectionRepository;
    private final UserRepository userRepository;

    @Transactional
    public SectionResponse createSection(Long userId, SectionRequest sectionRequest) {

        // TODO: Replace with helper func() that checks if USER exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Section createdSection = new Section();
        createdSection.setSectionName(sectionRequest.getSectionName());
        createdSection.setDisplayOrder(sectionRequest.getDisplayOrder());
        createdSection.setUser(user);

        return toSectionResponse(sectionRepository.save(createdSection));
    }

    @Transactional(readOnly = true)
    public List<SectionResponse> getAllSectionsByUser(Long userId) {

        // TODO: Replace with helper func() that checks if USER exists
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User not found");
        }

        return sectionRepository.findByUser_UserIdOrderByDisplayOrder(userId)
                .stream()
                .map(this::toSectionResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public SectionResponse updateSection(Long sectionId, SectionRequest sectionRequest) {

        Section updatedSection = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new EntityNotFoundException("Section not found"));

        updatedSection.setSectionName(sectionRequest.getSectionName());
        updatedSection.setDisplayOrder(sectionRequest.getDisplayOrder());

        return toSectionResponse(sectionRepository.save(updatedSection));
    }

    private SectionResponse toSectionResponse(Section section) {

        SectionResponse sectionResponse = new SectionResponse();

        sectionResponse.setSectionId(section.getSectionId());
        sectionResponse.setSectionName(section.getSectionName());
        sectionResponse.setDisplayOrder(section.getDisplayOrder());
        sectionResponse.setCreatedAt(section.getCreatedAt());
        sectionResponse.setUpdatedAt(section.getUpdatedAt());

        return sectionResponse;
    }

    @Transactional
    public SectionResponse deleteSection(Long sectionId) {

        Section section = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new EntityNotFoundException("Section not found"));

        SectionResponse deletedSectionResponse = toSectionResponse(section);

        sectionRepository.delete(section);

        return deletedSectionResponse;
    }
}