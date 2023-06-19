package com.example.afisha.services;

import com.example.afisha.entities.EventEntity;
import com.example.afisha.entities.UserActionStateEntity;
import com.example.afisha.entities.UserInnerStateEntity;
import com.example.afisha.repos.EventRepo;
import com.example.afisha.repos.UserActionStateRepo;
import com.example.afisha.repos.UserInnerStateRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;
import java.util.Optional;

import static com.example.afisha.services.TelegramBotUtils.*;

@Slf4j
@Component
public class AfishaHnHBot extends TelegramLongPollingBot {

    @Value("${bot.name}")
    private String botUsername;

    private final EventRepo eventRepo;
    private final UserInnerStateRepo userInnerStateRepo;
    private final UserActionStateRepo userActionStateRepo;
    private final AfishaService afishaService;

    @Autowired
    public AfishaHnHBot(@Value("${bot.token}") String botToken,
                        EventRepo eventRepo,
                        UserInnerStateRepo userInnerStateRepo,
                        UserActionStateRepo userActionStateRepo,
                        AfishaService afishaService
    ) {
        super(botToken);
        this.eventRepo = eventRepo;
        this.userInnerStateRepo = userInnerStateRepo;
        this.userActionStateRepo = userActionStateRepo;
        this.afishaService = afishaService;
    }

    @Override
    public void onUpdateReceived(Update update) {
        SendMessage message = new SendMessage();
        Long chatId;

        if (update.hasMessage() && update.getMessage().hasText()) {
            chatId = update.getMessage().getChatId();
            message.setChatId(chatId);
            String inputText = update.getMessage().getText();
            Long userTgId = update.getMessage().getFrom().getId();

            Optional<UserInnerStateEntity> userStateOpt = userInnerStateRepo.findByUserTgId(userTgId);

            if (userStateOpt.isPresent()) {
                UserInnerStateEntity userInnerStateEntity = userStateOpt.get();
                afishaService.showMainMenu(message, inputText, userTgId, userInnerStateEntity);
            } else {
                afishaService.showTextForInnerRegistration(message, userTgId);
            }
        }

        if (update.hasCallbackQuery()) {
            chatId = update.getCallbackQuery().getMessage().getChatId();
            message.setChatId(chatId);

            Long userTgId = chatId;
            String inputData = update.getCallbackQuery().getData();

            switch (inputData) {
                case GET_EVENTS -> afishaService.showEventList(message);
                case REGISTER_TO_EVENT -> afishaService.showEventMenuForRegistration(message, userTgId);
                case CANCEL_REGISTRATION -> {
                    Optional<UserActionStateEntity> userActionStateOptional = userActionStateRepo.findByUserTgId(userTgId);
                    List<EventEntity> events = eventRepo.findByUserTgId(userTgId);

                    if (userActionStateOptional.isEmpty() || events.isEmpty()) {
                        message.setText("Вы еще никуда не регистрировались.");
                    } else {
                        UserActionStateEntity userActionState = userActionStateOptional.get();
                        afishaService.showEventMenuForCancellation(message, userTgId, userActionState);
                    }
                }
                case MY_REGISTRATIONS -> afishaService.showRegistrationList(message, userTgId);
                default -> afishaService.makeActionWithRegisteredEvent(message, userTgId, inputData);
            }
        }

        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Неожиданная ошибка {0}", e);
        }
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

}
