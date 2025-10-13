package com.codewithmike.eventify.event;

import com.codewithmike.eventify.security.SecurityUtil;
import com.codewithmike.eventify.user.User;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class EventService {

    private final EventRepository repository;
    private final EventMapper mapper;

    public EventService(EventRepository repository, EventMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public Page<EventDto> fetchAllEvents(Pageable pageable) {
        User currentUser = SecurityUtil.currentUser();
        if (currentUser == null) {
            throw new RuntimeException("Unauthenticated");
        }

        Page<Event> events = repository.findByOwnerId(currentUser.getId(), pageable);
        return events.map(mapper::toDto);
    }


    public EventDto createEvent(EventDto dto) {
        User u = SecurityUtil.currentUser();
        if (u == null) throw new RuntimeException("Unauthenticated");

        Event e = mapper.toEntity(dto);
        e.setOwner(u);
        e = repository.save(e);
        return mapper.toDto(e);
    }

    public Optional<EventDto> updateEvent(UUID id, EventDto dto) {
        User u = SecurityUtil.currentUser();
        return repository.findByIdAndOwnerId(id, u.getId()).map(existing -> {
            existing.setTitle(dto.getTitle());
            existing.setDescription(dto.getDescription());
            existing.setLocation(dto.getLocation());
            existing.setDate(dto.getDate());
            return mapper.toDto(repository.save(existing));
        });
    }

    public Optional<EventDto> patchEvent(UUID id, EventDto dto) {
        User u = SecurityUtil.currentUser();
        return repository.findByIdAndOwnerId(id, u.getId()).map(existing -> {
            if (dto.getTitle() != null) existing.setTitle(dto.getTitle());
            if (dto.getDescription() != null) existing.setDescription(dto.getDescription());
            if (dto.getLocation() != null) existing.setLocation(dto.getLocation());
            if (dto.getDate() != null) existing.setDate(dto.getDate());
            return mapper.toDto(repository.save(existing));
        });
    }

    public boolean deleteEvent(UUID id) {
        User u = SecurityUtil.currentUser();
        return repository.findByIdAndOwnerId(id, u.getId()).map(existing -> {
            repository.delete(existing);
            return true;
        }).orElse(false);
    }

    public Page<Event> searchEvents(String title, String description, String location,
                                    LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        User u = SecurityUtil.currentUser();

        Specification<Event> ownerSpec = (root, query, cb) ->
                cb.equal(root.get("owner").get("id"), u.getId());

        Specification<Event> spec = Specification.allOf(
                ownerSpec,
                EventSpecifications.hasTitle(title),
                EventSpecifications.hasDescription(description),
                EventSpecifications.hasLocation(location),
                EventSpecifications.isBetweenDates(startDate, endDate)
        );

        return repository.findAll(spec, pageable);
    }

}
