package com.codewithmike.eventify.event;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EventMapper {

    EventDto toDto(Event event);
    Event toEntity(EventDto dto);
}
