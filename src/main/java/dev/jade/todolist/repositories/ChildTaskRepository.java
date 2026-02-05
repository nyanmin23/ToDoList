package dev.jade.todolist.repositories;

import dev.jade.todolist.models.ChildTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChildTaskRepository extends JpaRepository<ChildTask, Long> {

    Optional<ChildTask> findByChildTaskId(Long childTaskId);

    List<ChildTask> findByParentTask_ParentTaskIdOrderByDisplayOrder(Long parentTaskId);
}