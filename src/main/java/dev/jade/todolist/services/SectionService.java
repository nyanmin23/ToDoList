package dev.jade.todolist.services;

import dev.jade.todolist.dto.SectionDTO;
import dev.jade.todolist.exceptions.EntityNotFoundException;
import dev.jade.todolist.models.Section;
import dev.jade.todolist.models.User;
import dev.jade.todolist.repositories.SectionRepository;
import dev.jade.todolist.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SectionService {

    private final SectionRepository sectionRepository;
    private final UserRepository userRepository;

    public SectionDTO createSection(
            Long userId,
            SectionDTO sectionDTO
    ) {
        User user = userRepository.findById(userId)
                .orElseThrow(
                        () -> new EntityNotFoundException("User not found")
                );

        Section section = new Section();
        section.setSectionName(sectionDTO.getSectionName());
        section.setUser(user);

        return mapToSectionDTO(sectionRepository.save(section));
    }

    private SectionDTO mapToSectionDTO(Section section) {
        SectionDTO sectionDTO = new SectionDTO();
        sectionDTO.setSectionName(section.getSectionName());

        return sectionDTO;
    }

    public SectionDTO updateSection(
            Long sectionId,
            SectionDTO sectionDTO
    ) {
        Section section = sectionRepository.findBySectionId(sectionId)
                .orElseThrow(
                        () -> new EntityNotFoundException("Section not found")
                );

        section.setSectionName(sectionDTO.getSectionName());

        return mapToSectionDTO(sectionRepository.save(section));
    }
}