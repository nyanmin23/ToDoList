package dev.jade.todolist.models;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
public class OrderEntity extends AuditableEntity {

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder;

}
