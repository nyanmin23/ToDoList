package dev.jade.todolist.models;

import jakarta.persistence.*;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import java.time.Instant;

@MappedSuperclass  // Not an entity, just schema inheritance
@DynamicInsert    // Omit nulls in INSERT
@Getter
@Setter
public abstract class BaseEntity {

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
}
