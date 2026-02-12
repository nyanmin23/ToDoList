package dev.jade.todolist.domain.enums;

public enum InsertPosition {
    TOP,      // Insert at the beginning
    BOTTOM,   // Insert at the end
    BEFORE,   // Insert before specific item (requires referenceId)
    AFTER     // Insert after specific item (requires referenceId)
}
