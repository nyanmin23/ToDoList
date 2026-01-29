package dev.jade.todolist.controllers;

import dev.jade.todolist.dto.SectionDTO;
import dev.jade.todolist.services.SectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PutMapping("/{sectionId")
    public ResponseEntity<SectionDTO> updateSection(
            @PathVariable Long sectionId,
            @RequestBody SectionDTO sectionDTO
    ) {
        SectionDTO updatedSection = sectionService.updateSection(sectionId, sectionDTO);

        return ResponseEntity.ok(updatedSection);
    }



}
