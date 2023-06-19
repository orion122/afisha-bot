package com.example.afisha.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "users", indexes = @Index(columnList = "user_tg_id", unique = true))
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_tg_id")
    private Long userTgId;

    @Column(name = "fio")
    private String fio;

    public UserEntity(Long userTgId, String fio) {
        this.userTgId = userTgId;
        this.fio = fio;
    }

}