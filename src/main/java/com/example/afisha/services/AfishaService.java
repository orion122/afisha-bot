package com.example.afisha.services;

import com.example.afisha.entities.EventEntity;
import com.example.afisha.entities.UserActionStateEntity;
import com.example.afisha.entities.UserInnerStateEntity;
import com.example.afisha.models.UserActionState;
import com.example.afisha.models.UserInnerState;
import com.example.afisha.repos.EventRepo;
import com.example.afisha.repos.UserActionStateRepo;
import com.example.afisha.repos.UserInnerStateRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.example.afisha.models.UserActionState.CANCELLATION;
import static com.example.afisha.models.UserActionState.REGISTRATION;
import static com.example.afisha.services.EventRegistrationService.TryRegisterState;
import static com.example.afisha.services.EventRegistrationService.TryUnregisterState;
import static com.example.afisha.services.TelegramBotUtils.*;

@Service
@RequiredArgsConstructor
public class AfishaService {
    public static final String MAIN_MENU_TITLE = "Главное меню:";
    public static final int MAX_ACTION_ATTEMPTS_COUNT = 100;
    private final EventRepo eventRepo;
    private final UserInnerStateRepo userInnerStateRepo;
    private final EventRegistrationService eventRegistrationService;
    private final UserActionStateRepo userActionStateRepo;
    private final UserRegistrationService userRegistrationService;

    public void showRegistrationList(SendMessage message, Long userTgId) {
        List<EventEntity> events;
        events = eventRepo.findByUserTgId(userTgId);
        String text;

        if (events.size() > 0) {
            text = "Вы регистрировались на следующие мероприятия: \n\n" + getEventListText(events);
        } else {
            text = "Вы еще никуда не регистрировались.";
        }
        message.setText(text);
    }

    public void showEventMenuForCancellation(SendMessage message, Long userTgId, UserActionStateEntity userActionState) {
        List<EventEntity> events = eventRepo.findByUserTgId(userTgId);
        userActionState.setState(CANCELLATION);
        userActionStateRepo.save(userActionState);
        setReplyMarkup(message, getEventsInlineKeyboard(events), "Выберите событие для отмены регистрации: ");
    }

    public void showEventMenuForRegistration(SendMessage message, Long userTgId) {
        List<EventEntity> events = eventRepo.findNotRegisteredByUserTgId(userTgId);
        Optional<UserActionStateEntity> userActionStateOptional = userActionStateRepo.findByUserTgId(userTgId);
        UserActionStateEntity userActionState;

        if (userActionStateOptional.isEmpty()) {
            userActionState = new UserActionStateEntity(userTgId, REGISTRATION);
        } else {
            userActionState = userActionStateOptional.get();
            userActionState.setState(REGISTRATION);
        }

        userActionStateRepo.save(userActionState);

        setReplyMarkup(message, getEventsInlineKeyboard(events), "Выберите событие для регистрации: ");
    }

    public void showEventList(SendMessage message) {
        List<EventEntity> events = eventRepo.findAll();
        String text = "Список всех мероприятий: \n\n" + getEventListText(events);
        message.setText(text);
    }

    public void showTextForInnerRegistration(SendMessage message, Long userTgId) {
        UserInnerStateEntity userInnerStateEntity = new UserInnerStateEntity(userTgId, UserInnerState.NEW);
        userInnerStateRepo.save(userInnerStateEntity);
        message.setText("Введите ФИО для регистрации");
    }

    public void showMainMenu(SendMessage message, String inputText, Long userTgId, UserInnerStateEntity userInnerStateEntity) {

        InlineKeyboardMarkup mainInlineKeyboard = getMainInlineKeyboard();

        if (userInnerStateEntity.getState().equals(UserInnerState.NEW)) {
            userRegistrationService.userRegistration(inputText, userTgId, userInnerStateEntity);
        }

        setReplyMarkup(message, mainInlineKeyboard, MAIN_MENU_TITLE);
    }

    public void makeActionWithRegisteredEvent(SendMessage message, Long userTgId, String inputData) {
        Long eventId = Long.parseLong(inputData);
        Optional<EventEntity> eventOptional = eventRepo.findById(eventId);
        Optional<UserActionStateEntity> userActionStateOptional = userActionStateRepo.findByUserTgId(userTgId);

        if (eventOptional.isPresent() && userActionStateOptional.isPresent()) {
            String text;
            EventEntity event = eventOptional.get();
            UserActionState currentUserActionState = userActionStateOptional.get().getState();

            switch (currentUserActionState) {
                case REGISTRATION -> {
                    text = optimisticRegister(userTgId, eventId).getMessage();
                    message.setText(text + ": " + event.getName());
                }
                case CANCELLATION -> {
                    text = optimisticUnregister(userTgId, eventId).getMessage();
                    message.setText(text + ": " + event.getName());
                }
            }
        }
    }

    public TryRegisterState optimisticRegister(Long userTgId, Long eventId) {

        for (int i = 0; i < MAX_ACTION_ATTEMPTS_COUNT; i++) {
            TryRegisterState state = eventRegistrationService.tryToRegister(userTgId, eventId);

            if (Set.of(TryRegisterState.SUCCESS, TryRegisterState.NO_FREE_SEATS, TryRegisterState.ALREADY_REGISTERED).contains(state)) {
                return state;
            }
        }

        return TryRegisterState.FAILURE;
    }

    public TryUnregisterState optimisticUnregister(Long userTgId, Long eventId) {

        for (int i = 0; i < MAX_ACTION_ATTEMPTS_COUNT; i++) {
            TryUnregisterState state = eventRegistrationService.tryToUnregister(userTgId, eventId);

            if (Set.of(TryUnregisterState.SUCCESS, TryUnregisterState.NOT_FOUND).contains(state)) {
                return state;
            }
        }

        return TryUnregisterState.FAILURE;
    }
}
