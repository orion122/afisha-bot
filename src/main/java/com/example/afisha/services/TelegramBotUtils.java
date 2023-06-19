package com.example.afisha.services;

import com.example.afisha.entities.EventEntity;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TelegramBotUtils {
    public static final String GET_EVENTS = "getEvents";
    public static final String REGISTER_TO_EVENT = "registerToEvent";
    public static final String CANCEL_REGISTRATION = "cancelRegistration";
    public static final String MY_REGISTRATIONS = "myRegistrations";
    public static final Map<String, String> MAIN_MENU_MAP = Map.of(
            "Список мероприятий", GET_EVENTS,
            "Регистрация на мероприятие", REGISTER_TO_EVENT,
            "Отмена регистрации", CANCEL_REGISTRATION,
            "Мои регистрации", MY_REGISTRATIONS
    );

    private TelegramBotUtils() {}

    public static String getEventListText(List<EventEntity> events) {
        return events.stream()
                .map(e -> e.getName() + ": " + e.getDescription())
                .collect(Collectors.joining(";\n"));
    }

    public static void setReplyMarkup(SendMessage message, InlineKeyboardMarkup inlineKeyboardMarkup, String text) {
        message.setReplyMarkup(inlineKeyboardMarkup);
        message.setText(text);
    }

    public static InlineKeyboardMarkup getEventsInlineKeyboard(List<EventEntity> events) {

        List<InlineKeyboardButton> buttons = events
                .stream()
                .map(e -> {
                    InlineKeyboardButton eventsButton = new InlineKeyboardButton(e.getName());
                    eventsButton.setCallbackData(e.getId().toString());
                    return eventsButton;
                })
                .toList();

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(List.of(buttons));
        return inlineKeyboardMarkup;
    }

    public static InlineKeyboardMarkup getMainInlineKeyboard() {

        List<InlineKeyboardButton> buttons = MAIN_MENU_MAP.entrySet().stream()
                .map(entry -> {
                    InlineKeyboardButton eventsButton = new InlineKeyboardButton(entry.getKey());
                    eventsButton.setCallbackData(entry.getValue());
                    return eventsButton;
                })
                .toList();

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(List.of(buttons));
        return inlineKeyboardMarkup;
    }
}
