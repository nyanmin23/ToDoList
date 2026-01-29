package dev.jade.todolist.models;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
public abstract class BaseTaskEntity extends BaseEntity {

    @Builder.Default
    @Column(nullable = false)
    private Priority priority = Priority.GOBLIN;

    @Column(nullable = false)
    private boolean isCompleted = false;

    @PrePersist
    protected void setTaskDefaultValues() {
        if (priority == null) priority = Priority.GOBLIN;
    }
}
