package dev.jade.todolist.repositories;

import dev.jade.todolist.models.ParentTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParentTaskRepository extends JpaRepository<ParentTask, Long> {

    List<ParentTask> findBySection_SectionIdOrderByDisplayOrder(Long sectionId);

    boolean existsByParentTaskId(Long parentTaskId);
}