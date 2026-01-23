package dev.jade.todolist;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ToDoListApplication {

    public static void main(String[] args) {
        SpringApplication.run(ToDoListApplication.class, args);
    }

}

/**
 * TODO
 * 1. DBMS like Postgres, MySQL is suitable for storing ToDoList tasks
 *
 */
