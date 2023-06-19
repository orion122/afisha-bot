package com.example.afisha.entities;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Getter
@Table(name = "events")
public class EventEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "beginning")
    private LocalDateTime beginning;

    @Column(name = "seats")
    private Integer seats;

    @Column(name = "free_seats_count")
    private Integer freeSeatsCount;

}