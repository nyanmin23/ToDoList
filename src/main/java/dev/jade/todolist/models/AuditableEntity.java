package dev.jade.todolist.models;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@MappedSuperclass
public abstract class AuditableEntity {

    @Column(name = "created_at", insertable = false, nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", insertable = false, nullable = false, updatable = false)
    private Instant updatedAt;

}