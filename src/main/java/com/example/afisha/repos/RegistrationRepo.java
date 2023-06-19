package com.example.afisha.repos;

import com.example.afisha.entities.EventEntity;
import com.example.afisha.entities.RegistrationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegistrationRepo extends JpaRepository<RegistrationEntity, Long> {
    RegistrationEntity findByUserTgIdAndEventId(Long userTgId, Long eventId);

    Boolean existsByUserTgIdAndEventId(Long userTgId, Long eventId);

    void deleteByUserTgIdAndEventId(Long userTgId, Long eventId);

}