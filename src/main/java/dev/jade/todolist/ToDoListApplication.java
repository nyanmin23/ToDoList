package dev.jade.todolist;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class ToDoListApplication {

    public static void main(String[] args) {
        SpringApplication.run(ToDoListApplication.class, args);
    }

}

/**
 * Must implement Autmatic Display Ordering (LexoRank Alogrithm)
 * Must implement REST API pagination and possibly sorting
 * Finish implementing GlobalEceptionHandlers
 */