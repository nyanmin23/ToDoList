package dev.jade.todolist.mapstruct.mappers;

import dev.jade.todolist.configs.MapStructConfig;
import dev.jade.todolist.dtos.requests.AuthRequest;
import dev.jade.todolist.dtos.responses.AuthResponse;
import dev.jade.todolist.models.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapStructConfig.class)
public interface UserMapper {

    // Entity → Response
    AuthResponse toResponse(User user);

    // Request → Entity (for create)
    User toEntity(AuthRequest request);

    // Request → Entity (for update)
    void updateEntityFromRequest(AuthRequest request, @MappingTarget User user);

}
