package dev.jade.todolist.repositories;

import dev.jade.todolist.models.ChildTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChildTaskRepository extends JpaRepository<ChildTask, Long> {
    
}
