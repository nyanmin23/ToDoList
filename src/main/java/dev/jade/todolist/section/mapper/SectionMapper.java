package dev.jade.todolist.section.mapper;

import dev.jade.todolist.config.MapStructConfig;
import dev.jade.todolist.section.dto.request.SectionRequest;
import dev.jade.todolist.section.dto.response.SectionResponse;
import dev.jade.todolist.section.entity.Section;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapStructConfig.class)
public interface SectionMapper {

    SectionResponse toResponse(Section section);

    Section toEntity(SectionRequest request);

    void updateEntityFromRequest(SectionRequest request, @MappingTarget Section section);

}
