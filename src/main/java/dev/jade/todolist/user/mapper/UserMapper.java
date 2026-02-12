package dev.jade.todolist.user.mapper;

import dev.jade.todolist.config.MapStructConfig;
import dev.jade.todolist.user.dto.request.AuthRequest;
import dev.jade.todolist.user.dto.response.AuthResponse;
import dev.jade.todolist.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapStructConfig.class)
public interface UserMapper {

    AuthResponse toResponse(User user);

    User toEntity(AuthRequest request);

    void updateEntityFromRequest(AuthRequest request, @MappingTarget User user);

}
