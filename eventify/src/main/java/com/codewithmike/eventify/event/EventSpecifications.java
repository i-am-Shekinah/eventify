package com.codewithmike.eventify.event;

import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class EventSpecifications {

    public static Specification<Event> hasTitle(String title) {
        return (root, query, cb) ->
                title == null
                        ? null
                        : cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%");
    }

    public static Specification<Event> hasDescription(String description) {
        return (root, query, cb) ->
                description == null
                        ? null
                        : cb.like(cb.lower(root.get("description")), "%" + description.toLowerCase() + "%");
    }

    public static Specification<Event> hasLocation(String location) {
        return (root, query, cb) ->
                location == null
                        ? null
                        : cb.like(cb.lower(root.get("location")), "%" + location.toLowerCase() + "%");
    }

    // filter by date range
    public static Specification<Event> isBetweenDates(LocalDateTime start, LocalDateTime end) {
        return (root, query, cb) -> {
            if (start != null && end != null) {
                return cb.between(root.get("date"), start, end);
            } else if (start != null) {
                return cb.greaterThanOrEqualTo(root.get("date"), start);
            } else if (end != null) {
                return cb.lessThanOrEqualTo(root.get("date"), end);
            } else {
                return null;
            }
        };
    }


}
