package dev.jade.todolist.section.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class SectionResponse {

    private Long sectionId;
    private String sectionName;
    private String rank;
    private Instant createdAt;
    private Instant updatedAt;
}
