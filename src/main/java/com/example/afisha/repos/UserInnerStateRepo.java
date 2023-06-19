package com.example.afisha.repos;

import com.example.afisha.entities.UserInnerStateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserInnerStateRepo extends JpaRepository<UserInnerStateEntity, Long> {
    Optional<UserInnerStateEntity> findByUserTgId(Long userTgId);

}