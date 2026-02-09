package dev.jade.todolist.mapstruct.mappers;


import dev.jade.todolist.dtos.requests.SectionRequest;
import dev.jade.todolist.dtos.responses.SectionResponse;
import dev.jade.todolist.mapstruct.config.MapStructConfig;
import dev.jade.todolist.models.Section;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapStructConfig.class)
public interface SectionMapper {

    SectionResponse toResponse(Section section);

    Section toEntity(SectionRequest request);

    void updateEntityFromRequest(SectionRequest request, @MappingTarget Section section);

}
