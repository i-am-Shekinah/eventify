package com.codewithmike.eventify.participant;


import com.google.common.base.Preconditions;
import jakarta.servlet.http.Part;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/participants")
public class ParticipantController {
    private final ParticipantService participantService;

    public ParticipantController(ParticipantService participantService) {
        this.participantService = Preconditions.checkNotNull(
                participantService,
                "participantService cannot be null"
        );
    }

    // upload csv for a specific event
    @PostMapping("/upload/{eventId}")
    public Map<String, Object> uploadCsv (
            @PathVariable UUID eventId,
            @RequestParam("file")MultipartFile file
    ) throws Exception {
        return participantService.addParticipantsFromCsv(eventId, file);
    }

    // Get all participants for a specific event
    @GetMapping("event/{eventId}")
    public List<Participant> getParticipants(@PathVariable UUID eventId) {
        return participantService.getParticipantsForEvent(eventId);
    }
}
