package com.codewithmike.eventify.event;


import com.google.common.base.Preconditions;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/events")
public class EventController {
    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = Preconditions.checkNotNull(eventService, "eventService cannot be null");
    }

    @GetMapping
    public List<Event> fetchAllEvents() {
        return eventService.fetchAllEvents();
    }

    @PostMapping
    public ResponseEntity<EventDto> createEvent(@RequestBody EventDto eventDto) {
        EventDto createdEvent = eventService.createEvent(eventDto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .header("Location", "api/events/" + createdEvent.getId())
                .body(createdEvent);
    }


    @PutMapping("/{id}")
    public ResponseEntity<EventDto> updateEvent(
            @PathVariable UUID id,
            @RequestBody EventDto updatedEventDto
    ) {
        return eventService.updateEvent(id, updatedEventDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<EventDto> patchEvent(
            @PathVariable UUID id,
            @RequestBody EventDto eventDto
    ) {
        return eventService.patchEvent(id, eventDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable UUID id) {
        boolean deleted = eventService.deleteEvent(id);

        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/search")
    public List<Event> searchEvents(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String location,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)LocalDateTime endDate
            ) {
        return eventService.searchEvents(title, description, location, startDate, endDate);
    }
}
