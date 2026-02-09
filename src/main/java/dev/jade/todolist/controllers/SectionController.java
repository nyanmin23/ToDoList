package dev.jade.todolist.controllers;

import dev.jade.todolist.dtos.requests.SectionRequest;
import dev.jade.todolist.dtos.responses.SectionResponse;
import dev.jade.todolist.security.CustomUserDetails;
import dev.jade.todolist.services.SectionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sections")
public class SectionController {

    private final SectionService sectionService;

    @PostMapping
    public ResponseEntity<SectionResponse> createSection(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @Valid @RequestBody SectionRequest request
    ) {
        Long userId = currentUser.getUserId();
        SectionResponse newSection = sectionService.createSection(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(newSection);
    }

    @GetMapping
    public ResponseEntity<List<SectionResponse>> getSections(
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        Long userId = currentUser.getUserId();
        List<SectionResponse> sections = sectionService.findSectionsByUser(userId);
        return ResponseEntity.ok(sections);
    }

    @PutMapping("/{sectionId}")
    public ResponseEntity<SectionResponse> updateSection(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable Long sectionId,
            @Valid @RequestBody SectionRequest request
    ) {
        Long userId = currentUser.getUserId();
        SectionResponse updated = sectionService.updateSection(userId, sectionId, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{sectionId}")
    public ResponseEntity<Void> deleteSection(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable Long sectionId
    ) {
        Long userId = currentUser.getUserId();
        sectionService.deleteSection(userId, sectionId);
        return ResponseEntity.noContent().build();
    }
}