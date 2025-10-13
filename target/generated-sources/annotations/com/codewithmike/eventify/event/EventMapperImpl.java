package com.codewithmike.eventify.event;

import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-10-13T10:26:44+0100",
    comments = "version: 1.6.3, compiler: javac, environment: Java 25 (Oracle Corporation)"
)
@Component
public class EventMapperImpl implements EventMapper {

    @Override
    public EventDto toDto(Event event) {
        if ( event == null ) {
            return null;
        }

        EventDto.EventDtoBuilder eventDto = EventDto.builder();

        eventDto.id( event.getId() );
        eventDto.title( event.getTitle() );
        eventDto.description( event.getDescription() );
        eventDto.location( event.getLocation() );
        eventDto.date( event.getDate() );

        return eventDto.build();
    }

    @Override
    public Event toEntity(EventDto dto) {
        if ( dto == null ) {
            return null;
        }

        Event.EventBuilder event = Event.builder();

        event.title( dto.getTitle() );
        event.description( dto.getDescription() );
        event.location( dto.getLocation() );
        event.date( dto.getDate() );

        return event.build();
    }
}
