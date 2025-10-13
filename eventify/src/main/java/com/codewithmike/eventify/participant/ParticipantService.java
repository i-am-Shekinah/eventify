package com.codewithmike.eventify.participant;

import com.codewithmike.eventify.event.Event;
import com.codewithmike.eventify.event.EventRepository;
import com.codewithmike.eventify.security.SecurityUtil;
import com.codewithmike.eventify.user.User;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class ParticipantService {

    private final ParticipantRepository participantRepository;
    private final EventRepository eventRepository;

    public ParticipantService(ParticipantRepository participantRepository, EventRepository eventRepository) {
        this.participantRepository = participantRepository;
        this.eventRepository = eventRepository;
    }

    public Map<String, Object> addParticipantsFromCsv(UUID eventId, MultipartFile file) throws Exception {
        User u = SecurityUtil.currentUser();
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        // check ownership
        if (!event.getOwner().getId().equals(u.getId())) {
            throw new RuntimeException("Access denied");
        }

        List<Participant> added = new ArrayList<>();
        List<String> skipped = new ArrayList<>();
        Set<String> existing = new HashSet<>();
        participantRepository.findByEventId(eventId, Pageable.ofSize(Integer.MAX_VALUE))
                .forEach(p -> existing.add(p.getEmail().toLowerCase()));

        try (CSVParser parser = CSVParser.parse(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8),
                CSVFormat.DEFAULT.builder()
                        .setHeader("firstname", "lastname", "email", "phone", "status")
                        .setSkipHeaderRecord(true)
                        .setIgnoreHeaderCase(true)
                        .setTrim(true)
                        .build()
        )) {
            for (CSVRecord r : parser) {
                String firstname = r.get("firstname");
                String lastname = r.get("lastname");
                String email = r.get("email").toLowerCase();
                String phone = r.isMapped("phone") ? r.get("phone") : "";
                String statusStr = r.isMapped("status") ? r.get("status") : "PENDING";
                InvitationStatus status;
                try { status = InvitationStatus.valueOf(statusStr.toUpperCase()); }
                catch (Exception ex) { status = InvitationStatus.PENDING; }

                if (existing.contains(email)) {
                    skipped.add(email);
                    continue;
                }

                Participant p = Participant.builder()
                        .firstname(firstname)
                        .lastname(lastname)
                        .email(email)
                        .phoneNumber(phone)
                        .invitationStatus(status)
                        .event(event)
                        .build();

                added.add(participantRepository.save(p));
                existing.add(email);
            }
        }

        Map<String,Object> out = new HashMap<>();
        out.put("addedCount", added.size());
        out.put("skippedCount", skipped.size());
        out.put("skippedEmails", skipped);
        out.put("addedParticipants", added);
        return out;
    }

    public Page<Participant> getParticipantsForEvent(UUID eventId, Pageable pageable) {
        User u = SecurityUtil.currentUser();
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new RuntimeException("Event not found"));
        if (!event.getOwner().getId().equals(u.getId())) throw new RuntimeException("Access denied");
        return participantRepository.findByEventId(eventId, pageable);
    }

    public Participant updateInvitationStatus(UUID eventId, UUID participantId, InvitationStatus status) {
        User u = SecurityUtil.currentUser();
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new RuntimeException("Event not found or access denied"));
        if (!event.getOwner().getId().equals(u.getId())) throw new RuntimeException("Access denied");

        Participant p = participantRepository.findByIdAndEventId(participantId, eventId)
                .orElseThrow(() -> new RuntimeException("Participant not found"));

        p.setInvitationStatus(status);
        return participantRepository.save(p);
    }
}
