package dev.jade.todolist.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
public abstract class AbstractTask extends AuditableEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Priority priority;

    @Column(nullable = false)
    private boolean completed = false;

    @PrePersist
    protected void setTaskDefaults() {
        super.onCreate();
        if (this.priority == null) {
            this.priority = Priority.GOBLIN;
        }
    }
}