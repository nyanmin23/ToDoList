package dev.jade.todolist.mapper;

public interface BaseMapper<DTO, Entity> {
    DTO toDTO(Entity entity);

    Entity toEntity(DTO dto);
}
