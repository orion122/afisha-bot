package com.example.afisha.repos;

import com.example.afisha.entities.UserActionStateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserActionStateRepo extends JpaRepository<UserActionStateEntity, Long> {

    Optional<UserActionStateEntity> findByUserTgId(Long userTgId);

}
