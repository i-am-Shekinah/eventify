package com.codewithmike.eventify.event;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EventMapper {

    EventDto toDto(Event event);
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "owner", ignore = true)
    Event toEntity(EventDto dto);
}
