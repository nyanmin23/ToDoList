package dev.jade.todolist.task.parent.repository;

import dev.jade.todolist.task.parent.entity.ParentTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
interface ParentTaskRepository extends JpaRepository<ParentTask, Long> {

    /**
     * Find parent tasks by section with pagination
     */
    @Query("""
                SELECT pt FROM ParentTask pt
                WHERE pt.section.sectionId = :sectionId
                AND pt.updatedAt <= :listVersion
                AND (:cursor IS NULL OR pt.rank > :cursor)
                ORDER BY pt.rank ASC
                LIMIT :limit
            """)
    List<ParentTask> findBySectionIdWithPagination(
            @Param("sectionId") Long sectionId,
            @Param("cursor") String cursor,
            @Param("listVersion") Instant listVersion,
            @Param("limit") int limit
    );

    /**
     * Find parent task by ID with ownership validation (through section)
     */
    @Query("""
                SELECT pt FROM ParentTask pt
                WHERE pt.parentTaskId = :taskId
                AND pt.section.sectionId = :sectionId
                AND pt.section.user.userId = :userId
            """)
    Optional<ParentTask> findByIdAndSectionIdAndUserId(
            @Param("taskId") Long taskId,
            @Param("sectionId") Long sectionId,
            @Param("userId") Long userId
    );

    /**
     * Get first/last rank for section (for insertion)
     */
    @Query("""
                SELECT pt.rank FROM ParentTask pt
                WHERE pt.section.sectionId = :sectionId
                ORDER BY pt.rank ASC
                LIMIT 1
            """)
    String findFirstRankBySectionId(@Param("sectionId") Long sectionId);

    @Query("""
                SELECT pt.rank FROM ParentTask pt
                WHERE pt.section.sectionId = :sectionId
                ORDER BY pt.rank DESC
                LIMIT 1
            """)
    String findLastRankBySectionId(@Param("sectionId") Long sectionId);
}