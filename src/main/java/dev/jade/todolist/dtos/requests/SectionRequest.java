package dev.jade.todolist.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class SectionRequest {

    @NotBlank(message = "Name is required")
    private String sectionName;

}