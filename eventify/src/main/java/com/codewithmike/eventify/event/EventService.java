package com.codewithmike.eventify.event;


import com.google.common.base.Preconditions;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class EventService {
    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = Preconditions.checkNotNull(
                eventRepository,
                "eventRepository cannot be null"
        );
    }

    public Event createEvent(Event event) {
        return eventRepository.save(event);
    }


    public Optional<Event> updateEvent(UUID id, Event updatedEvent) {

        // check event exists
        Preconditions.checkNotNull(
                id,
                "Unable to update event - Event with ID '%s' not found",
                id
        );

        return eventRepository.findById(id)
                .map(event -> {
                    event.setTitle(updatedEvent.getTitle());
                    event.setDescription(updatedEvent.getDescription());
                    event.setLocation(updatedEvent.getLocation());
                    event.setDate(updatedEvent.getDate());
                    return eventRepository.save(event);
                });
    }

    public void deleteEvent(UUID id) {
        Preconditions.checkNotNull(
                id,
                "Unable to delete event - Event with ID '%s' not found",
                id
        );

        eventRepository.deleteById(id);
    }

    public List<Event> fetchAllEvents() {
        return eventRepository.findAll();
    }


    /*
    public List<Event> searchEvents() {

    } */

}
