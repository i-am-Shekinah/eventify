package com.codewithmike.eventify.participant;

import com.codewithmike.eventify.event.Event;
import com.codewithmike.eventify.event.EventRepository;
import com.google.common.base.Preconditions;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.web.multipart.MultipartFile;


import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ParticipantService {
    private EventRepository eventRepository;
    private ParticipantRepository participantRepository;

    public ParticipantService(ParticipantRepository participantRepository, EventRepository eventRepository) {
        this.participantRepository = Preconditions.checkNotNull(
                participantRepository,
                "participantRepository cannot be null"
        );
        this.eventRepository = Preconditions.checkNotNull(
                eventRepository,
                "eventRepository cannot be null"
        );
    }


    public Map<String, Object> addParticipantsFromCsv(UUID eventId, MultipartFile file) throws Exception {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found with ID: " + eventId));

        List<Participant> participantsAdded = new ArrayList<>();
        List<String> duplicatesSkipped = new ArrayList<>();

        // Load existing emails for duplicate checking
        Set<String> existingEmails = new HashSet<>();
        participantRepository.findByEventId(eventId)
                .forEach(p -> existingEmails.add(p.getEmail().toLowerCase()));

        // Parse CSV
        try (CSVParser parser = CSVParser.parse(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8),
                CSVFormat.DEFAULT
                        .withHeader("firstname", "lastname", "email", "phone", "status")
                        .withSkipHeaderRecord(true)
                        .withIgnoreHeaderCase(true)
                        .withTrim())) {

            for (CSVRecord record : parser) {
                String firstname = record.get("firstname");
                String lastname = record.get("lastname");
                String email = record.get("email").toLowerCase();
                String phone = record.isMapped("phone") ? record.get("phone") : "";
                String status = record.isMapped("status") ? record.get("status") : "PENDING";

                if (existingEmails.contains(email)) {
                    duplicatesSkipped.add(email);
                    continue;
                }

                Participant participant = Participant.builder()
                        .firstname(firstname)
                        .lastname(lastname)
                        .email(email)
                        .phoneNumber(phone)
                        .invitationStatus(status)
                        .build();

                participantsAdded.add(participantRepository.save(participant));
                existingEmails.add(email);
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("addedCount", participantsAdded.size());
        result.put("skippedCount", duplicatesSkipped.size());
        result.put("skippedEmails", duplicatesSkipped);
        result.put("addedParticipants", participantsAdded);

        return result;
    }

    public List<Participant> getParticipantsForEvent(UUID eventId) {
        return participantRepository.findByEventId(eventId);
    }
}