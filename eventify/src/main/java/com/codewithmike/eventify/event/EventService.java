package com.codewithmike.eventify.event;


import com.google.common.base.Preconditions;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class EventService {
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;

    public EventService(EventRepository eventRepository, EventMapper eventMapper) {
        this.eventRepository = Preconditions.checkNotNull(
                eventRepository,
                "eventRepository cannot be null"
        );
        this.eventMapper = Preconditions.checkNotNull(
                eventMapper,
                "eventMapper cannot be null"
        );
    }

    public EventDto createEvent(EventDto eventDto) {
        Event event = eventMapper.toEntity(eventDto);
        Event savedEvent = eventRepository.save(event);
        return eventMapper.toDto(savedEvent);
    }


    public Optional<EventDto> updateEvent(UUID id, EventDto updatedEventDto) {

        // check event exists
        Preconditions.checkNotNull(
                id,
                "Unable to update event - Event with ID '%s' not found",
                id
        );

        return eventRepository.findById(id)
                .map(event -> {
                    event.setTitle(updatedEventDto.getTitle());
                    event.setDescription(updatedEventDto.getDescription());
                    event.setLocation(updatedEventDto.getLocation());
                    event.setDate(updatedEventDto.getDate());
                    return eventRepository.save(event);
                })
                .map(eventMapper::toDto);
    }

    public Optional<EventDto> patchEvent(UUID id, EventDto partialEventDto) {
        // check event exists
        Preconditions.checkNotNull(
                id,
                "Unable to update event - Event with ID '%s' not found",
                id
        );

        return eventRepository.findById(id)
                .map(event -> {
                    if (partialEventDto.getTitle() != null) {
                        event.setTitle(partialEventDto.getTitle());
                    }
                    if (partialEventDto.getDescription() != null) {
                        event.setDescription(partialEventDto.getDescription());
                    }
                    if (partialEventDto.getLocation() != null) {
                        event.setLocation(partialEventDto.getLocation());
                    }
                    if (partialEventDto.getDate() != null) {
                        event.setDate(partialEventDto.getDate());
                    }
                    return eventRepository.save(event);
                })
                .map(eventMapper::toDto);
    }

    public boolean deleteEvent(UUID id) {
        Preconditions.checkNotNull(
                id,
                "Unable to delete event - Event with ID '%s' not found",
                id
        );

        if (eventRepository.existsById(id)) {
            eventRepository.deleteById(id);
            return  true;
        } else {
            return false;
        }

    }

    public List<Event> fetchAllEvents() {
        return eventRepository.findAll();
    }



    public List<Event> searchEvents(
            String title,
            String description,
            String location,
            LocalDateTime startDate,
            LocalDateTime endDate
    ) {
        return eventRepository.findAll(
                Specification.allOf(
                        EventSpecifications.hasTitle(title),
                        EventSpecifications.hasDescription(description),
                        EventSpecifications.hasLocation(location),
                        EventSpecifications.isBetweenDates(startDate, endDate)
                )
        );
    }

}
