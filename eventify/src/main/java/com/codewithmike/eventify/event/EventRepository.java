package com.codewithmike.eventify.event;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EventRepository extends JpaRepository<Event, UUID>, JpaSpecificationExecutor<Event> {
    List<Event> findByTitleContainingIgnoreCase(String title);
    List<Event> findByDescriptionContainingIgnoreCase(String description);
    List<Event> findByLocationContainingIgnoreCase(String location);
    List<Event> findByDate(LocalDateTime date);
    Page<Event> findByOwnerId(UUID ownerId, Pageable pageable);
    Optional<Event> findByIdAndOwnerId(UUID id, UUID ownerId);
}
