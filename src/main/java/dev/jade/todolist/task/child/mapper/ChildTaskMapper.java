package dev.jade.todolist.task.child.mapper;

import dev.jade.todolist.config.MapStructConfig;
import dev.jade.todolist.task.child.dto.request.ChildTaskRequest;
import dev.jade.todolist.task.child.dto.response.ChildTaskResponse;
import dev.jade.todolist.task.child.entity.ChildTask;
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
