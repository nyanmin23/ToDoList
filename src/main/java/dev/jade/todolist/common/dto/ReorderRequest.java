package dev.jade.todolist.common.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
class ReorderRequest {

    @NotNull(message = "Item ID is required")
    private Long itemId;

    @NotBlank(message = "New rank is required")
    @Size(max = 50, message = "Rank cannot exceed 50 characters")
    private String newRank;
}

