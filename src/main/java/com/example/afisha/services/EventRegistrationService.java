package com.example.afisha.services;

import com.example.afisha.entities.RegistrationEntity;
import com.example.afisha.repos.EventRepo;
import com.example.afisha.repos.RegistrationRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EventRegistrationService {
    private final RegistrationRepo registrationRepo;
    private final EventRepo eventRepo;


    /**
     * SUCCESS - зарегистрироваться удалось
     * NO_FREE_SEATS - все места заняты
     * TRY_AGAIN - нужно попробовать еще раз
     */
    @Transactional
    public TryRegisterState tryToRegister(Long userTgId, Long eventId) {

        Integer freeSeatsCount = eventRepo.getReferenceById(eventId).getFreeSeatsCount();
        Boolean isRegisterExists = registrationRepo.existsByUserTgIdAndEventId(userTgId, eventId);

        if (isRegisterExists) {
            return TryRegisterState.ALREADY_REGISTERED;
        }

        if (freeSeatsCount == 0) {
            return TryRegisterState.NO_FREE_SEATS;
        }

        Integer updatedRows = eventRepo.takeSeat(freeSeatsCount, eventId);

        if (updatedRows == 0) {
            return TryRegisterState.TRY_AGAIN;
        }

        registrationRepo.save(new RegistrationEntity(userTgId, eventId));

        return TryRegisterState.SUCCESS;
    }

    /**
     * SUCCESS - удалить регистрацию удалось
     * REGISTER_NOT_FOUND - вашей записи не было
     * TRY_AGAIN - нужно попробовать еще раз
     */
    @Transactional
    public TryUnregisterState tryToUnregister(Long userTgId, Long eventId) {

        Integer freeSeatsCount = eventRepo.getReferenceById(eventId).getFreeSeatsCount();
        Boolean isRegisterExists = registrationRepo.existsByUserTgIdAndEventId(userTgId, eventId);

        if (!isRegisterExists) {
            return TryUnregisterState.NOT_FOUND;
        }

        Integer updatedRows = eventRepo.freeSeat(freeSeatsCount, eventId);

        if (updatedRows == 0) {
            return TryUnregisterState.TRY_AGAIN;
        }

        registrationRepo.deleteByUserTgIdAndEventId(userTgId, eventId);

        return TryUnregisterState.SUCCESS;

    }

    public enum TryRegisterState {
        SUCCESS("Вы зарегистрировались на событие"),
        NO_FREE_SEATS("Не осталось мест на событие"),
        ALREADY_REGISTERED("Вы уже зарегистрированы на событие"),
        TRY_AGAIN("Необходимо попробовать еще раз"),
        FAILURE("Не удалось зарегистрироваться на событие");

        private final String message;

        TryRegisterState(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }

    public enum TryUnregisterState {
        SUCCESS("Вы отменили регистрацию на событие"),
        NOT_FOUND("Не найдено событие"),
        TRY_AGAIN("Необходимо попробовать еще раз"),
        FAILURE("Не удалось отменить регистрацию на событие");

        private final String message;

        TryUnregisterState(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }

}