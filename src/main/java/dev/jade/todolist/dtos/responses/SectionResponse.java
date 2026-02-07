package dev.jade.todolist.dtos.responses;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class SectionResponse {

    private Long sectionId;

    private String sectionName;

    private Integer displayOrder;

    private Instant createdAt;

    private Instant updatedAt;

}
