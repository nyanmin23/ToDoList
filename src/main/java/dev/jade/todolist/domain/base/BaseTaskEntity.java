package dev.jade.todolist.domain.base;

import dev.jade.todolist.domain.enums.Priority;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
abstract class BaseTaskEntity extends RankedEntity {

    @Column(name = "deadline")
    private java.time.Instant deadline;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false)
    private Priority priority;

    @Column(name = "is_completed", nullable = false)
    private boolean isCompleted;

    @Column(name = "completed_at")
    private java.time.Instant completedAt;

    @PreUpdate
    @PrePersist
    protected void onUpdate() {
        if (this.isCompleted && this.completedAt == null) {
            this.completedAt = java.time.Instant.now();
        } else if (!this.isCompleted) {
            this.completedAt = null;
        }
    }
}