package ru.javazen.telegram.bot.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.ApiContext;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.generics.BotSession;
import ru.javazen.telegram.bot.CompositeBot;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Service
public class BotInitializer {
    private static final Logger LOGGER = LoggerFactory.getLogger(BotInitializer.class);

    static {
        ApiContextInitializer.init();
    }

    private BotSession session;
    @Autowired
    private CompositeBot bot;

    @PostConstruct
    public void initBots() throws TelegramApiRequestException {
        TelegramBotsApi botsApi = new TelegramBotsApi();
        session = botsApi.registerBot(bot);
    }

    @PreDestroy
    public void destroy() {
        if (session != null) {
            session.stop();
        }
    }
}
