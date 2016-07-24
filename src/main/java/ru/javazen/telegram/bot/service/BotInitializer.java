package ru.javazen.telegram.bot.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.javazen.telegram.bot.Bot;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Map;

@Service
public class BotInitializer {
    private static final Logger LOGGER = LoggerFactory.getLogger(BotInitializer.class);

    @Value("${callbackUrl}")
    private String callbackUrl;

    @Resource(name = "botMap")
    private Map<String, Bot> bots;

    @PostConstruct
    public void initBots(){
        LOGGER.debug("Start init bots");
        for (Map.Entry<String, Bot> entry: bots.entrySet()){
            initBot(entry.getKey(), entry.getValue());
        }
    }

    private void initBot(String name, Bot bot){
        bot.getService().setWebHook(callbackUrl + name);
        bot.onStart();
    }
}
