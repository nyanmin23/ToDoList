package dev.jade.todolist.mapstruct.mappers;

import dev.jade.todolist.configs.MapStructConfig;
import dev.jade.todolist.dtos.requests.ParentTaskRequest;
import dev.jade.todolist.dtos.responses.ParentTaskResponse;
import dev.jade.todolist.models.ParentTask;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapStructConfig.class)
public interface ParentTaskMapper {

    // Entity → Response
    ParentTaskResponse toResponse(ParentTask parentTask);

    // Request → Entity (for create)
    ParentTask toEntity(ParentTaskRequest request);

    // Request → Entity (for update)
    void updateEntityFromRequest(ParentTaskRequest request, @MappingTarget ParentTask parentTask);
}
