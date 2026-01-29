package dev.jade.todolist.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SectionDTO {

    @NotNull
    @NotEmpty
    private String sectionName;

}
