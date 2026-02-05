package dev.jade.todolist.repositories;

import dev.jade.todolist.models.ChildTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChildTaskRepository extends JpaRepository<ChildTask, Long> {

    List<ChildTask> findByParentTask_ParentTaskIdOrderByDisplayOrder(Long parentTaskId);

    boolean existsByChildTaskId(Long childTaskId);

}