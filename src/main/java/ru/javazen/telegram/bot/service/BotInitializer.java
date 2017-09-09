package ru.javazen.telegram.bot.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.javazen.telegram.bot.TelegramBot;
import ru.javazen.telegram.bot.UpdateProvider;

import javax.annotation.PostConstruct;

@Service
public class BotInitializer {
    private static final Logger LOGGER = LoggerFactory.getLogger(BotInitializer.class);

    @Autowired
    private TelegramBot bot;

    @Autowired
    private UpdateProvider updateProvider;

    @PostConstruct
    public void initBots(){
        updateProvider.addBot(bot);
    }
}
