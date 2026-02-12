package dev.jade.todolist.task.child.repository;

import dev.jade.todolist.task.child.entity.ChildTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
interface ChildTaskRepository extends JpaRepository<ChildTask, Long> {

    /**
     * Find child tasks by parent task with pagination
     */
    @Query("""
                SELECT ct FROM ChildTask ct
                WHERE ct.parentTask.parentTaskId = :parentTaskId
                AND ct.updatedAt <= :listVersion
                AND (:cursor IS NULL OR ct.rank > :cursor)
                ORDER BY ct.rank ASC
                LIMIT :limit
            """)
    List<ChildTask> findByParentTaskIdWithPagination(
            @Param("parentTaskId") Long parentTaskId,
            @Param("cursor") String cursor,
            @Param("listVersion") Instant listVersion,
            @Param("limit") int limit
    );

    /**
     * Find child task with full ownership validation chain
     */
    @Query("""
                SELECT ct FROM ChildTask ct
                WHERE ct.childTaskId = :childTaskId
                AND ct.parentTask.parentTaskId = :parentTaskId
                AND ct.parentTask.section.sectionId = :sectionId
                AND ct.parentTask.section.user.userId = :userId
            """)
    Optional<ChildTask> findByIdWithFullOwnershipValidation(
            @Param("childTaskId") Long childTaskId,
            @Param("parentTaskId") Long parentTaskId,
            @Param("sectionId") Long sectionId,
            @Param("userId") Long userId
    );
}