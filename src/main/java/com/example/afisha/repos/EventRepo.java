package com.example.afisha.repos;

import com.example.afisha.entities.EventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepo extends JpaRepository<EventEntity, Long> {

    @Query("select e from EventEntity e where e.id in (select r.eventId from RegistrationEntity r where r.userTgId = :userTgId)")
    List<EventEntity> findByUserTgId(@Param("userTgId") Long userTgId);

    @Query("select e from EventEntity e where e.id not in (select r.eventId from RegistrationEntity r where r.userTgId = :userTgId)")
    List<EventEntity> findNotRegisteredByUserTgId(@Param("userTgId") Long userTgId);

    @Modifying
    @Query("update EventEntity e set e.freeSeatsCount = (:freeSeatsCount - 1) where e.freeSeatsCount = :freeSeatsCount and e.id = :eventId")
    Integer takeSeat(@Param("freeSeatsCount") Integer freeSeatsCount, @Param("eventId") Long eventId);

    @Modifying
    @Query("update EventEntity e set e.freeSeatsCount = (:freeSeatsCount + 1) where e.freeSeatsCount = :freeSeatsCount and e.id = :eventId")
    Integer freeSeat(@Param("freeSeatsCount") Integer freeSeatsCount, @Param("eventId") Long eventId);

}