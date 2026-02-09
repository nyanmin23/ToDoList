package dev.jade.todolist.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class SectionRequest {

    @NotBlank(message = "Name is required")
    private String sectionName;

    @NotNull(message = "Display order is required")
    private Integer displayOrder;

}