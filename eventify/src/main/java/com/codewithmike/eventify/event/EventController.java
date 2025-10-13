package com.codewithmike.eventify.event;


import com.google.common.base.Preconditions;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/events")
@Tag(
        name = "Event Management",
        description = "Endpoints for managing and searching events."
)
public class EventController {
    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = Preconditions.checkNotNull(eventService, "eventService cannot be null");
    }


    @Operation(
            summary = "Get all events",
            description = "Fetch a list of all available events",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "List of all events fetched successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = Event.class))
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Unable to fetch events",
                            content = @Content
                    )
            }
    )
    @GetMapping
    public Page<EventDto> fetchMyEvents(Pageable pageable) {
        return eventService.fetchAllEvents(pageable);
    }

    @Operation(
            summary = "Create a new event",
            description = "Adds a new event and returns the created event with its ID",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Event created successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = EventDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid event data",
                            content = @Content
                    )
            }
    )
    @PostMapping
    public ResponseEntity<EventDto> createEvent(@RequestBody EventDto eventDto) {
        EventDto createdEvent = eventService.createEvent(eventDto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .header("Location", "api/events/" + createdEvent.getId())
                .body(createdEvent);
    }



    @Operation(
            summary = "Update every field of an event",
            description = "Replace an existing event with new details",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Event updated successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = EventDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid event data",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Event not found",
                            content = @Content
                    )
            }
    )
    @PutMapping("/{id}")
    public ResponseEntity<EventDto> updateEvent(
            @Parameter(description = "UUID of the event to update")
            @PathVariable UUID id,
            @RequestBody EventDto updatedEventDto
    ) {
        return eventService.updateEvent(id, updatedEventDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @Operation(
            summary = "Partially update an event",
            description = "Update specific fields of an existing event",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Event successfully updated",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = EventDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid event data",
                            content = @Content
                    )
            }
    )
    @PatchMapping("/{id}")
    public ResponseEntity<EventDto> patchEvent(
            @Parameter(description = "UUID of the event to update")
            @PathVariable UUID id,
            @RequestBody EventDto eventDto
    ) {
        return eventService.patchEvent(id, eventDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @Operation(
            summary = "Delete an event",
            description = "Deletes an existing event by its unique ID. Returns 204 if successful or 404 if not found.",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Event deleted successfully",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Event not found",
                            content = @Content
                    )
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(
            @Parameter(description = "UUID of the event to delete")
            @PathVariable UUID id
    ) {
        boolean deleted = eventService.deleteEvent(id);

        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }



    @Operation(
            summary = "Search user's events with optional filters",
            description = """
                Fetch events belonging to the authenticated user, filtered by title, description, location,
                or date range. Supports pagination and sorting via query parameters.
                """,
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Filtered list of events fetched successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Event.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized – user not authenticated",
                            content = @Content
                    )
            }
    )
    @GetMapping("/search")
    public ResponseEntity<Page<Event>> searchEvents(
            @Parameter(description = "Filter events by title (case-insensitive)")
            @RequestParam(required = false) String title,

            @Parameter(description = "Filter events by description (case-insensitive)")
            @RequestParam(required = false) String description,

            @Parameter(description = "Filter events by location (case-insensitive)")
            @RequestParam(required = false) String location,

            @Parameter(
                    description = "Filter events starting from this date and time (ISO format: yyyy-MM-dd'T'HH:mm:ss)"
            )
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,

            @Parameter(
                    description = "Filter events ending before this date and time (ISO format: yyyy-MM-dd'T'HH:mm:ss)"
            )
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,

            @Parameter(description = "Pagination and sorting parameters")
            Pageable pageable
    ) {
        Page<Event> results = eventService.searchEvents(title, description, location, startDate, endDate, pageable);
        return ResponseEntity.ok(results);
    }


}
