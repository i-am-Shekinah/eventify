package com.codewithmike.eventify.event;

import com.codewithmike.eventify.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String title;
    private String description;
    private String location;
    private LocalDateTime date;

    @ManyToOne(optional = false)
    @JoinColumn(name = "owner_id")
    private User owner;

}
