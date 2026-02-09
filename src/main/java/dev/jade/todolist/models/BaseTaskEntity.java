package dev.jade.todolist.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@MappedSuperclass
public abstract class BaseTaskEntity extends OrderEntity {

    @Column(name = "deadline")
    private Instant deadline;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false)
    private Priority priority;

    @Column(name = "is_completed", nullable = false)
    private boolean isCompleted;

    @Column(name = "completed_at")
    private Instant completedAt;

    @PreUpdate
    @PrePersist
    protected void onUpdate() {
        if (this.isCompleted && this.completedAt == null) {
            this.completedAt = Instant.now();
        } else if (!this.isCompleted) {
            this.completedAt = null;
        }
    }
}