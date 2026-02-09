package dev.jade.todolist.repositories;

import dev.jade.todolist.models.ChildTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChildTaskRepository extends JpaRepository<ChildTask, Long> {

    List<ChildTask> findByParentTask_ParentTaskIdOrderByDisplayOrder(Long parentTaskId);

    @Query("""
                SELECT ct FROM ChildTask ct
                JOIN ct.parentTask pt
                JOIN pt.section s
                WHERE ct.childTaskId = :childTaskId
                AND s.user.userId = :userId
            """)
    Optional<ChildTask> findByIdAndUserId(
            @Param("childTaskId") Long childTaskId,
            @Param("userId") Long userId
    );

    @Query("""
                SELECT ct FROM ChildTask ct
                WHERE ct.childTaskId = :childTaskId
                AND ct.parentTask.parentTaskId = :parentTaskId
                AND ct.parentTask.section.user.userId = :userId
            """)
    Optional<ChildTask> findByIdAndParentIdAndUserId(
            @Param("childTaskId") Long childTaskId,
            @Param("parentTaskId") Long parentTaskId,
            @Param("userId") Long userId
    );
}