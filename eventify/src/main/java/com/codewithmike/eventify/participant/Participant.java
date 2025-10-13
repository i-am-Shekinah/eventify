package com.codewithmike.eventify.participant;


import com.codewithmike.eventify.event.Event;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Participant {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String firstname;
    private String lastname;
    private String email;
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private InvitationStatus invitationStatus = InvitationStatus.PENDING;


    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;
}
