package dev.jade.todolist.common.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
class BatchReorderRequest {

    @NotEmpty(message = "At least one item must be reordered")
    @Size(max = 100, message = "Cannot reorder more than 100 items at once")
    private java.util.List<ReorderRequest> items;
}
