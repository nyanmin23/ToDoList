package dev.jade.todolist.section.controller;


import dev.jade.todolist.common.pagination.PagedResponse;
import dev.jade.todolist.common.pagination.PaginationRequest;
import dev.jade.todolist.section.dto.request.SectionRequest;
import dev.jade.todolist.section.dto.response.SectionResponse;
import dev.jade.todolist.section.service.SectionService;
import dev.jade.todolist.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Updated SectionController with:
 * - LexoRank ordering
 * - Snapshot-based pagination
 * - Proper exception hierarchy usage
 * - Reordering endpoints
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sections")
public class SectionController {

    private final SectionService sectionService;

    /**
     * Create a new section
     * POST /api/sections
     * <p>
     * Request body can include:
     * - rank: Explicit LexoRank position
     * - insertPosition: TOP or BOTTOM (if rank not provided)
     * <p>
     * Returns 201 CREATED with created section
     */
    @PostMapping
    public ResponseEntity<SectionResponse> createSection(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @Valid @RequestBody SectionRequest request
    ) {
        Long userId = currentUser.getUserId();
        log.debug("Creating section for user {}", userId);

        SectionResponse response = sectionService.createSection(userId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get all sections for current user with pagination
     * GET /api/sections?pageSize=20&cursor=abc&listVersion=2024-01-15T10:00:00Z
     * <p>
     * Query parameters:
     * - pageSize: Number of items per page (default 20, max 100)
     * - cursor: LexoRank of last item from previous page (null for first page)
     * - listVersion: Snapshot version from first request (null for first request)
     * - includeTotalCount: Whether to include total count (default false)
     * <p>
     * Returns paginated response with:
     * - items: List of sections
     * - listVersion: Snapshot version to use for subsequent requests
     * - nextCursor: Cursor for next page
     * - hasMore: Whether there are more pages
     * - totalCount: Total count (if requested)
     */
    @GetMapping
    public ResponseEntity<PagedResponse<SectionResponse>> getSections(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @Valid @ModelAttribute PaginationRequest paginationRequest
    ) {
        Long userId = currentUser.getUserId();
        log.debug("Fetching sections for user {} with pagination {}", userId, paginationRequest);

        PagedResponse<SectionResponse> response = sectionService.findSectionsByUser(
                userId,
                paginationRequest
        );

        return ResponseEntity.ok(response);
    }

    /**
     * Get all sections without pagination (for small datasets)
     * GET /api/sections/all
     * <p>
     * Returns all sections ordered by rank
     */
    @GetMapping("/all")
    public ResponseEntity<List<SectionResponse>> getAllSections(
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        Long userId = currentUser.getUserId();
        log.debug("Fetching all sections for user {}", userId);

        // Implementation would call a non-paginated service method
        // List<SectionResponse> sections = sectionService.findAllSectionsByUser(userId);

        return ResponseEntity.ok(List.of()); // Placeholder
    }

    /**
     * Update section
     * PUT /api/sections/{sectionId}
     * <p>
     * Can update:
     * - sectionName
     * - rank (for reordering)
     * <p>
     * Returns 200 OK with updated section
     * Returns 404 if section not found or user doesn't own it
     */
    @PutMapping("/{sectionId}")
    public ResponseEntity<SectionResponse> updateSection(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable Long sectionId,
            @Valid @RequestBody SectionRequest request
    ) {
        Long userId = currentUser.getUserId();
        log.debug("Updating section {} for user {}", sectionId, userId);

        SectionResponse response = sectionService.updateSection(userId, sectionId, request);

        return ResponseEntity.ok(response);
    }

    /**
     * Delete section
     * DELETE /api/sections/{sectionId}
     * <p>
     * Cascades to all parent tasks and child tasks
     * Returns 204 NO CONTENT
     * Returns 404 if section not found or user doesn't own it
     */
    @DeleteMapping("/{sectionId}")
    public ResponseEntity<Void> deleteSection(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable Long sectionId
    ) {
        Long userId = currentUser.getUserId();
        log.debug("Deleting section {} for user {}", sectionId, userId);

        sectionService.deleteSection(userId, sectionId);

        return ResponseEntity.noContent().build();
    }

    /**
     * Reorder section to new position
     * POST /api/sections/{sectionId}/reorder
     * <p>
     * Request body:
     * {
     * "prevRank": "abc",  // Rank of section before new position (null for top)
     * "nextRank": "xyz"   // Rank of section after new position (null for bottom)
     * }
     * <p>
     * Returns 200 OK with updated section containing new rank
     */
    @PostMapping("/{sectionId}/reorder")
    public ResponseEntity<SectionResponse> reorderSection(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable Long sectionId,
            @RequestBody ReorderRequest request
    ) {
        Long userId = currentUser.getUserId();
        log.debug("Reordering section {} for user {}", sectionId, userId);

        SectionResponse response = sectionService.reorderSection(
                userId,
                sectionId,
                request.getPrevRank(),
                request.getNextRank()
        );

        return ResponseEntity.ok(response);
    }

    /**
     * Batch reorder sections (for rebalancing)
     * POST /api/sections/reorder-batch
     * <p>
     * Request body:
     * {
     * "sectionIds": [1, 2, 3, 4, 5]  // Ordered list of section IDs
     * }
     * <p>
     * Generates evenly distributed ranks for all sections
     * Useful when ranks get too close after many insertions
     * <p>
     * Returns 200 OK with all updated sections
     */
    @PostMapping("/reorder-batch")
    public ResponseEntity<List<SectionResponse>> batchReorderSections(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @RequestBody BatchReorderSectionsRequest request
    ) {
        Long userId = currentUser.getUserId();
        log.debug("Batch reordering {} sections for user {}",
                request.getSectionIds().size(), userId);

        List<SectionResponse> responses = sectionService.batchReorderSections(
                userId,
                request.getSectionIds()
        );

        return ResponseEntity.ok(responses);
    }
}

/**
 * Request for reordering a single section
 */
@lombok.Getter
@lombok.Setter
class ReorderRequest {
    private String prevRank;  // Can be null for top position
    private String nextRank;  // Can be null for bottom position
}

/**
 * Request for batch reordering sections
 */
@lombok.Getter
@lombok.Setter
class BatchReorderSectionsRequest {
    @jakarta.validation.constraints.NotEmpty
    @jakarta.validation.constraints.Size(max = 100, message = "Cannot reorder more than 100 sections at once")
    private List<Long> sectionIds;
}