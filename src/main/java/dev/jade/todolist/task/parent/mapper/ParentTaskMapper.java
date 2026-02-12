package dev.jade.todolist.task.parent.mapper;

import dev.jade.todolist.config.MapStructConfig;
import dev.jade.todolist.task.parent.dto.request.ParentTaskRequest;
import dev.jade.todolist.task.parent.dto.response.ParentTaskResponse;
import dev.jade.todolist.task.parent.entity.ParentTask;
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
