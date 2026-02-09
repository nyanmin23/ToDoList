package dev.jade.todolist.mapstruct.mappers;

import dev.jade.todolist.dtos.requests.AuthRequest;
import dev.jade.todolist.dtos.responses.AuthResponse;
import dev.jade.todolist.mapstruct.config.MapStructConfig;
import dev.jade.todolist.models.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapStructConfig.class)
public interface UserMapper {

    AuthResponse toResponse(User user);

    User toEntity(AuthRequest request);

    void updateEntityFromRequest(AuthRequest request, @MappingTarget User user);

}
