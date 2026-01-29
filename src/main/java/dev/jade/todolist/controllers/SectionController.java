package dev.jade.todolist.controllers;

import dev.jade.todolist.dto.SectionDTO;
import dev.jade.todolist.services.SectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sections")
public class SectionController {

    private final SectionService sectionService;

    @PostMapping
    public ResponseEntity<SectionDTO> createSection(
            @RequestParam Long userId,
            @RequestBody SectionDTO sectionDTO
    ) {
        SectionDTO createdSection = sectionService.createSection(userId, sectionDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdSection);
    }

    @GetMapping
    public ResponseEntity<List<SectionDTO>> getAllSectionsByUserId(
            @RequestParam Long userId
    ) {
        List<SectionDTO> sections = sectionService.getAllSectionsByUserId(userId);

        return ResponseEntity.ok(sections);
    }

    @GetMapping("/{sectionId}")
    public ResponseEntity<SectionDTO> getSectionById(
            @PathVariable Long sectionId
    ) {
        SectionDTO section = sectionService.getSectionById(sectionId);

        return ResponseEntity.ok(section);
    }

    @PutMapping("/{sectionId}")
    public ResponseEntity<SectionDTO> updateSection(
            @PathVariable Long sectionId,
            @RequestBody SectionDTO sectionDTO
    ) {
        SectionDTO updatedSection = sectionService.updateSection(sectionId, sectionDTO);

        return ResponseEntity.ok(updatedSection);
    }

}
