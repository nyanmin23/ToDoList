package dev.jade.todolist.mapstruct.mappers;

import dev.jade.todolist.dtos.requests.ParentTaskRequest;
import dev.jade.todolist.dtos.responses.ParentTaskResponse;
import dev.jade.todolist.mapstruct.configs.MapStructConfig;
import dev.jade.todolist.models.ParentTask;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapStructConfig.class)
public interface ParentTaskMapper {

    ParentTaskResponse toResponse(ParentTask parentTask);

    @Mapping(target = "priority", defaultValue = "LOW")
//    @Mapping(target = "isCompleted", defaultValue = "false")
    ParentTask toEntity(ParentTaskRequest request);

    void updateEntityFromRequest(ParentTaskRequest request, @MappingTarget ParentTask parentTask);
}
