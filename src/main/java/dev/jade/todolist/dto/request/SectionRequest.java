package dev.jade.todolist.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SectionRequest {

    @NotNull(message = "Section name is required")
    @NotEmpty(message = "Section name cannot be empty")
    private String sectionName;

    @NotNull
    private Integer displayOrder;

}