package dev.jade.todolist.mapstruct.mappers;

import dev.jade.todolist.configs.MapStructConfig;
import dev.jade.todolist.dtos.requests.ChildTaskRequest;
import dev.jade.todolist.dtos.responses.ChildTaskResponse;
import dev.jade.todolist.models.ChildTask;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapStructConfig.class)
public interface ChildTaskMapper {

    // Entity → Response
    ChildTaskResponse toResponse(ChildTask childTask);

    // Request → Entity (for create)
    ChildTask toEntity(ChildTaskRequest request);

    // Request → Entity (for update)
    void updateEntityFromRequest(ChildTaskRequest request, @MappingTarget ChildTask childTask);

}
