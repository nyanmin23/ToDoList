package dev.jade.todolist.controllers;

import dev.jade.todolist.dto.request.SectionRequest;
import dev.jade.todolist.dto.response.SectionResponse;
import dev.jade.todolist.services.SectionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/{userId}/sections")
public class SectionController {

    private final SectionService sectionService;

    // Works fine
    @PostMapping
    public ResponseEntity<SectionResponse> addNewSection(
            @PathVariable Long userId,
            @Valid @RequestBody SectionRequest sectionRequest
    ) {
        SectionResponse newSection = sectionService.createSection(userId, sectionRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(newSection);
    }

    // Works fine
    @GetMapping
    public ResponseEntity<List<SectionResponse>> displayAllSectionsByUser(
            @PathVariable Long userId
    ) {
        List<SectionResponse> allSections = sectionService.getAllSectionsByUser(userId);

        return ResponseEntity.ok(allSections);
    }

    // Works fine
    @PutMapping("/{sectionId}")
    public ResponseEntity<SectionResponse> updateSection(
            @PathVariable Long sectionId,
            @Valid @RequestBody SectionRequest sectionRequest
    ) {
        SectionResponse updatedSection = sectionService.updateSection(sectionId, sectionRequest);

        return ResponseEntity.ok(updatedSection);
    }

    @DeleteMapping("/{sectionId}")
    public ResponseEntity<SectionResponse> deleteSection(
            @PathVariable Long sectionId
    ) {
        SectionResponse deletedSection = sectionService.deleteSection(sectionId);

        return ResponseEntity.ok(deletedSection);
    }

}
