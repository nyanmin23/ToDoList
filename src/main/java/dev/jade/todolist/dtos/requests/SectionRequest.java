package dev.jade.todolist.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SectionRequest {

    @NotBlank(message = "Name is required")
    private String sectionName;

    @NotNull(message = "Display order is required")
    private Integer displayOrder;

}