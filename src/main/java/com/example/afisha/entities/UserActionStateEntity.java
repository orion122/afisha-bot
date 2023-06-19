package com.example.afisha.entities;

import com.example.afisha.models.UserActionState;
import com.example.afisha.models.UserInnerState;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "user_action_states", indexes = @Index(columnList = "user_tg_id", unique = true))
public class UserActionStateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_tg_id")
    private Long userTgId;

    @Column(name = "state")
    @Enumerated(EnumType.STRING)
    private UserActionState state;

    public UserActionStateEntity(Long userTgId, UserActionState state) {
        this.userTgId = userTgId;
        this.state = state;
    }

}
