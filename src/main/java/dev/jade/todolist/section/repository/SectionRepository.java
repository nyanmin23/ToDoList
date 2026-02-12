package dev.jade.todolist.section.repository;

import dev.jade.todolist.section.entity.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Updated SectionRepository with LexoRank and snapshot pagination support.
 */
@Repository
public interface SectionRepository extends JpaRepository<Section, Long> {

    /**
     * Find sections by user with keyset pagination and snapshot semantics.
     * Orders by rank (LexoRank) for consistent ordering.
     *
     * @param userId      User ID
     * @param cursor      Last rank from previous page (null for first page)
     * @param listVersion Snapshot version to lock pagination to
     * @param limit       Number of items to fetch
     * @return List of sections
     */
    @Query("""
                SELECT s FROM Section s
                WHERE s.user.userId = :userId
                AND s.updatedAt <= :listVersion
                AND (:cursor IS NULL OR s.rank > :cursor)
                ORDER BY s.rank ASC
                LIMIT :limit
            """)
    List<Section> findByUserIdWithPagination(
            @Param("userId") Long userId,
            @Param("cursor") String cursor,
            @Param("listVersion") Instant listVersion,
            @Param("limit") int limit
    );

    /**
     * Count total sections for user at specific snapshot version
     */
    @Query("""
                SELECT COUNT(s) FROM Section s
                WHERE s.user.userId = :userId
                AND s.updatedAt <= :listVersion
            """)
    Long countByUserIdAtVersion(
            @Param("userId") Long userId,
            @Param("listVersion") Instant listVersion
    );

    /**
     * Find section by ID and user ID (ownership validation)
     */
    @Query("""
                SELECT s FROM Section s
                WHERE s.sectionId = :sectionId
                AND s.user.userId = :userId
            """)
    Optional<Section> findByIdAndUserId(
            @Param("sectionId") Long sectionId,
            @Param("userId") Long userId
    );

    /**
     * Check if section exists for user (ownership validation)
     */
    @Query("""
                SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END
                FROM Section s
                WHERE s.sectionId = :sectionId
                AND s.user.userId = :userId
            """)
    boolean existsByIdAndUserId(
            @Param("sectionId") Long sectionId,
            @Param("userId") Long userId
    );

    /**
     * Get first rank (smallest lexicographically) for user
     * Used when inserting at top
     */
    @Query("""
                SELECT s.rank FROM Section s
                WHERE s.user.userId = :userId
                ORDER BY s.rank ASC
                LIMIT 1
            """)
    String findFirstRankByUserId(@Param("userId") Long userId);

    /**
     * Get last rank (largest lexicographically) for user
     * Used when inserting at bottom
     */
    @Query("""
                SELECT s.rank FROM Section s
                WHERE s.user.userId = :userId
                ORDER BY s.rank DESC
                LIMIT 1
            """)
    String findLastRankByUserId(@Param("userId") Long userId);

    /**
     * Find all sections for user ordered by rank
     * Used for non-paginated queries (small datasets)
     */
    @Query("""
                SELECT s FROM Section s
                WHERE s.user.userId = :userId
                ORDER BY s.rank ASC
            """)
    List<Section> findByUserIdOrderByRank(@Param("userId") Long userId);

    /**
     * Check if rank already exists in user's sections (for duplicate detection)
     */
    @Query("""
                SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END
                FROM Section s
                WHERE s.user.userId = :userId
                AND s.rank = :rank
                AND (:excludeId IS NULL OR s.sectionId != :excludeId)
            """)
    boolean existsByUserIdAndRank(
            @Param("userId") Long userId,
            @Param("rank") String rank,
            @Param("excludeId") Long excludeId
    );
}