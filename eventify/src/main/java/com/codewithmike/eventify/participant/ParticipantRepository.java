package com.codewithmike.eventify.participant;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ParticipantRepository extends JpaRepository<Participant, UUID> {
    Page<Participant> findByEventId(UUID eventId, Pageable pageable);
    Optional<Participant> findByIdAndEventId(UUID id, UUID eventId);
}
