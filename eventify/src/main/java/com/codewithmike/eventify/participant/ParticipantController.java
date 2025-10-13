package com.codewithmike.eventify.participant;


import com.google.common.base.Preconditions;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/participants")
@Tag(
        name = "Participant Management",
        description = "Endpoints for uploading and retrieving participants for events"
)
public class ParticipantController {
    private final ParticipantService participantService;

    public ParticipantController(ParticipantService participantService) {
        this.participantService = Preconditions.checkNotNull(
                participantService,
                "participantService cannot be null"
        );
    }


    @Operation(
            summary = "Upload participants CSV for an event",
            description = "Uploads a CSV file containing participant details (firstname, lastname, email, phoneNumber, invitaionStatus). "
            + "Skips duplicate emails already registered for the event.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Participants successfully processed",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Map.class
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid file format or bad request",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Event not found",
                            content = @Content
                    )
            }
    )
    // upload csv for a specific event
    @PostMapping("/upload/{eventId}")
    public Map<String, Object> uploadCsv (
            @Parameter(description = "UUID of the event", required = true)
            @PathVariable UUID eventId,
            @Parameter(description = "CSV file containing participant data", required = true)
            @RequestParam("file")MultipartFile file
    ) throws Exception {
        return participantService.addParticipantsFromCsv(eventId, file);
    }


    @Operation(
            summary = "Get all participants for a specific event",
            description = "Returns all list of all participants registered for the given event ID",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "List of participants",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Participant.class)
                            )
                    ),

                    @ApiResponse(
                            responseCode = "404",
                            description = "Event not found",
                            content = @Content
                    )
            }
    )
    // Get all participants for a specific event
    @GetMapping("event/{eventId}")
    public Page<Participant> getParticipants(
            @Parameter(description = "UUID of the event", required = true)
            @PathVariable UUID eventId,

            @Parameter(description = "Pagination and sorting parameters")
            Pageable pageable
    ) {
        return participantService.getParticipantsForEvent(eventId, pageable);
    }
}
