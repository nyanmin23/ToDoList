package dev.jade.todolist.domain.base;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

/**
 * Base class for entities that require ordering using LexoRank.
 * Replaces the old OrderEntity with numeric displayOrder.
 */
@Getter
@Setter
@MappedSuperclass
public abstract class RankedEntity extends AuditableEntity {

    /**
     * LexoRank for ordering.
     * String-based rank allows insertion between items without reordering.
     * Indexed for efficient sorting queries.
     */
    @Column(name = "rank", nullable = false, length = 50)
    private String rank;
}

