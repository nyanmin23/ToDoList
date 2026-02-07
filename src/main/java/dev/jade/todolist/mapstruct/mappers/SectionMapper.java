package dev.jade.todolist.mapstruct.mappers;


import dev.jade.todolist.configs.MapStructConfig;
import dev.jade.todolist.dtos.requests.SectionRequest;
import dev.jade.todolist.dtos.responses.SectionResponse;
import dev.jade.todolist.models.Section;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapStructConfig.class)
public interface SectionMapper {

    // Entity → Response
    SectionResponse toResponse(Section section);

    // Request → Entity (for create)
    Section toEntity(SectionRequest request);

    // Request → Entity (for update)
    void updateEntityFromRequest(SectionRequest request, @MappingTarget Section section);

}
