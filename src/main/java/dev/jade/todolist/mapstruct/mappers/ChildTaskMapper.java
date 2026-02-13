package dev.jade.todolist.mapstruct.mappers;

import dev.jade.todolist.dtos.requests.ChildTaskRequest;
import dev.jade.todolist.dtos.responses.ChildTaskResponse;
import dev.jade.todolist.mapstruct.configs.MapStructConfig;
import dev.jade.todolist.models.ChildTask;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapStructConfig.class)
public interface ChildTaskMapper {

    ChildTaskResponse toResponse(ChildTask childTask);

    @Mapping(target = "priority", defaultValue = "LOW")
//    @Mapping(target = "isCompleted", defaultValue = "false")
    ChildTask toEntity(ChildTaskRequest request);

    void updateEntityFromRequest(ChildTaskRequest request, @MappingTarget ChildTask childTask);

}
