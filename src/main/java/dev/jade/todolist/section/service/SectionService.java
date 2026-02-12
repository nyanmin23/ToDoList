package dev.jade.todolist.section.service;

import dev.jade.todolist.common.exception.SectionNotFoundException;
import dev.jade.todolist.common.exception.UnauthorizedAccessException;
import dev.jade.todolist.common.exception.UserNotFoundException;
import dev.jade.todolist.common.pagination.PagedResponse;
import dev.jade.todolist.common.pagination.PaginationRequest;
import dev.jade.todolist.common.pagination.PaginationService;
import dev.jade.todolist.common.ranking.LexoRankService;
import dev.jade.todolist.domain.enums.InsertPosition;
import dev.jade.todolist.section.dto.request.SectionRequest;
import dev.jade.todolist.section.dto.response.SectionResponse;
import dev.jade.todolist.section.entity.Section;
import dev.jade.todolist.section.mapper.SectionMapper;
import dev.jade.todolist.section.repository.SectionRepository;
import dev.jade.todolist.user.entity.User;
import dev.jade.todolist.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Updated SectionService with LexoRank ordering and snapshot pagination.
 * Demonstrates proper exception hierarchy usage and database-managed timestamps.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SectionService {

    private final SectionRepository sectionRepository;
    private final UserRepository userRepository;
    private final LexoRankService lexoRankService;
    private final PaginationService paginationService;
    private final SectionMapper mapper;

    /**
     * Create a new section with LexoRank positioning
     */
    @Transactional
    public SectionResponse createSection(Long userId, SectionRequest request) {
        // Validate user exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        // Determine rank
        String rank = determineRank(userId, request);

        // Create section
        Section section = mapper.toEntity(request);
        section.setUser(user);
        section.setRank(rank);

        // Save (timestamps managed by database)
        Section saved = sectionRepository.save(section);

        log.info("Created section {} for user {}", saved.getSectionId(), userId);
        return mapper.toResponse(saved);
    }

    /**
     * Get all sections for user with pagination
     */
    @Transactional(readOnly = true)
    public PagedResponse<SectionResponse> findSectionsByUser(
            Long userId,
            PaginationRequest paginationRequest
    ) {
        // Validate user exists
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }

        // Get current database timestamp for snapshot version
        Instant currentVersion = Instant.now(); // In real impl, get from DB

        // Validate snapshot version if provided
        if (paginationRequest.getListVersion() != null) {
            paginationService.validateSnapshotVersion(paginationRequest.getListVersion());
        }

        // Determine snapshot version to use
        Instant snapshotVersion = paginationRequest.getListVersion() != null
                ? paginationRequest.getListVersion()
                : currentVersion;

        // Query with keyset pagination
        List<Section> sections = sectionRepository.findByUserIdWithPagination(
                userId,
                paginationRequest.getCursor(),
                snapshotVersion,
                paginationRequest.getPageSize() + 1 // Fetch one extra to check hasMore
        );

        // Determine if there are more pages
        boolean hasMore = sections.size() > paginationRequest.getPageSize();
        if (hasMore) {
            sections = sections.subList(0, paginationRequest.getPageSize());
        }

        // Get total count if requested
        Long totalCount = null;
        if (paginationRequest.isIncludeTotalCount()) {
            totalCount = sectionRepository.countByUserIdAtVersion(userId, snapshotVersion);
        }

        // Map to responses
        List<SectionResponse> responses = sections.stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());

        // Build paginated response
        return PagedResponse.<SectionResponse>builder()
                .items(responses)
                .listVersion(snapshotVersion)
                .nextCursor(hasMore && !sections.isEmpty()
                        ? sections.get(sections.size() - 1).getRank()
                        : null)
                .hasMore(hasMore)
                .totalCount(totalCount)
                .pageSize(paginationRequest.getPageSize())
                .build();
    }

    /**
     * Update section
     */
    @Transactional
    public SectionResponse updateSection(
            Long userId,
            Long sectionId,
            SectionRequest request
    ) {
        // Find section with ownership validation
        Section section = sectionRepository.findByIdAndUserId(sectionId, userId)
                .orElseThrow(() -> new SectionNotFoundException(sectionId, userId));

        // Update fields
        mapper.updateEntityFromRequest(request, section);

        // Update rank if provided
        if (request.getRank() != null) {
            section.setRank(request.getRank());
        }

        // Save (updatedAt managed by database trigger)
        Section updated = sectionRepository.save(section);

        log.info("Updated section {} for user {}", sectionId, userId);
        return mapper.toResponse(updated);
    }

    /**
     * Delete section
     */
    @Transactional
    public void deleteSection(Long userId, Long sectionId) {
        // Find section with ownership validation
        Section section = sectionRepository.findByIdAndUserId(sectionId, userId)
                .orElseThrow(() -> new SectionNotFoundException(sectionId, userId));

        // Delete (cascades to tasks)
        sectionRepository.delete(section);

        log.info("Deleted section {} for user {}", sectionId, userId);
    }

    /**
     * Reorder section to new position
     */
    @Transactional
    public SectionResponse reorderSection(
            Long userId,
            Long sectionId,
            String prevRank,
            String nextRank
    ) {
        // Find section with ownership validation
        Section section = sectionRepository.findByIdAndUserId(sectionId, userId)
                .orElseThrow(() -> new SectionNotFoundException(sectionId, userId));

        // Calculate new rank
        String newRank = lexoRankService.getRankBetween(prevRank, nextRank);
        section.setRank(newRank);

        // Save
        Section updated = sectionRepository.save(section);

        log.info("Reordered section {} to rank {}", sectionId, newRank);
        return mapper.toResponse(updated);
    }

    /**
     * Batch reorder sections (for rebalancing)
     */
    @Transactional
    public List<SectionResponse> batchReorderSections(
            Long userId,
            List<Long> sectionIds
    ) {
        // Validate all sections belong to user
        List<Section> sections = sectionRepository.findAllById(sectionIds);

        if (sections.size() != sectionIds.size()) {
            throw new SectionNotFoundException(null); // Some sections not found
        }

        for (Section section : sections) {
            if (!section.getUser().getUserId().equals(userId)) {
                throw new UnauthorizedAccessException("Section", section.getSectionId(), userId);
            }
        }

        // Generate new ranks
        String[] newRanks = lexoRankService.rebalanceRanks(sections.size());

        // Assign ranks
        for (int i = 0; i < sections.size(); i++) {
            sections.get(i).setRank(newRanks[i]);
        }

        // Save all
        List<Section> updated = sectionRepository.saveAll(sections);

        log.info("Rebalanced {} sections for user {}", sections.size(), userId);

        return updated.stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    // ========== PRIVATE HELPER METHODS ==========

    /**
     * Determine appropriate rank for new section based on request
     */
    private String determineRank(Long userId, SectionRequest request) {
        // If rank provided, use it
        if (request.getRank() != null) {
            return request.getRank();
        }

        // Otherwise, calculate based on insert position
        if (request.getInsertPosition() == null ||
                request.getInsertPosition() == InsertPosition.BOTTOM) {
            // Insert at bottom - get last section's rank
            String lastRank = sectionRepository.findLastRankByUserId(userId);
            return lastRank != null
                    ? lexoRankService.getRankAfter(lastRank)
                    : lexoRankService.getInitialRank();
        } else {
            // Insert at top - get first section's rank
            String firstRank = sectionRepository.findFirstRankByUserId(userId);
            return firstRank != null
                    ? lexoRankService.getRankBefore(firstRank)
                    : lexoRankService.getInitialRank();
        }
    }
}