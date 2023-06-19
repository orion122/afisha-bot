package com.example.afisha.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "registrations", indexes = @Index(columnList = "user_tg_id, event_id"))
public class RegistrationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;

    @Column(name = "user_tg_id")
    Long userTgId;

    @Column(name = "event_id")
    Long eventId;

    public RegistrationEntity(Long userTgId, Long eventId) {
        this.userTgId = userTgId;
        this.eventId = eventId;
    }
}
