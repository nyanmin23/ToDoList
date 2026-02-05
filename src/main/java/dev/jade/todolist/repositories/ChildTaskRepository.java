package dev.jade.todolist.repositories;

import dev.jade.todolist.models.ChildTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChildTaskRepository extends JpaRepository<ChildTask, Long> {

    /**
     * Find all child tasks belonging to a specific parent task.
     * <p>
     * Spring Data JPA method naming convention:
     * - "findAllBy" = find multiple records
     * - "ParentTask_ParentId" = navigate through 'parentTask' relationship and filter by 'parentId'
     * <p>
     * This assumes your ChildTask entity has a field called 'parentTask' which is a
     * relationship to the ParentTask entity, and that ParentTask has a field 'parentId'
     */
    List<ChildTask> findAllByParentTask_ParentTaskId(Long parentId);

    /**
     * Alternative explicit JPQL approach if preferred:
     *
     * @Query("SELECT ct FROM ChildTask ct WHERE ct.parentTask.parentId = :parentId")
     * List<ChildTask> findAllByParentId(@Param("parentId") Long parentId);
     */
}