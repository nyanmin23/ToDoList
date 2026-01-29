package dev.jade.todolist.repositories;

import dev.jade.todolist.models.ParentTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface ParentTaskRepository extends JpaRepository<ParentTask, Long> {

}
