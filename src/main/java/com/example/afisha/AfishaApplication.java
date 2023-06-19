package com.example.afisha;

import com.example.afisha.services.AfishaHnHBot;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.starter.TelegramBotStarterConfiguration;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@SpringBootApplication
@Import(TelegramBotStarterConfiguration.class)
public class AfishaApplication {

    public static void main(String[] args) {
        SpringApplication.run(AfishaApplication.class, args);
    }

}
