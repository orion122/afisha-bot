package com.example.afisha.services;

import com.example.afisha.entities.UserEntity;
import com.example.afisha.entities.UserInnerStateEntity;
import com.example.afisha.models.UserInnerState;
import com.example.afisha.repos.UserInnerStateRepo;
import com.example.afisha.repos.UserRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserRegistrationService {
    private final UserRepo userRepo;
    private final UserInnerStateRepo userInnerStateRepo;

    @Transactional
    public void userRegistration(String inputText, Long userTgId, UserInnerStateEntity userInnerStateEntity) {
        UserEntity user = new UserEntity(userTgId, inputText);
        userRepo.save(user);

        userInnerStateEntity.setState(UserInnerState.REGISTERED);
        userInnerStateRepo.save(userInnerStateEntity);
    }
}
