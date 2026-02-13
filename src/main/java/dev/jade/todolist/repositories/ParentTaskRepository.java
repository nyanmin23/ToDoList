package dev.jade.todolist.repositories;

import dev.jade.todolist.models.ParentTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParentTaskRepository extends JpaRepository<ParentTask, Long> {

    List<ParentTask> findBySection_SectionIdOrderByCreatedAt(Long sectionId);

    @Query("""
                SELECT pt FROM ParentTask pt
                WHERE pt.parentTaskId = :parentTaskId
                AND pt.section.user.userId = :userId
            """)
    Optional<ParentTask> findByIdAndUserId(
            @Param("parentTaskId") Long parentTaskId,
            @Param("userId") Long userId
    );

    @Query("""
                SELECT CASE WHEN COUNT(pt) > 0 THEN true ELSE false END
                FROM ParentTask pt
                WHERE pt.parentTaskId = :parentTaskId
                AND pt.section.user.userId = :userId
            """)
    boolean existsByIdAndUserId(
            @Param("parentTaskId") Long parentTaskId,
            @Param("userId") Long userId
    );
}