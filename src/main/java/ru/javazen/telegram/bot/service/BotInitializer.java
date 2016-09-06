package ru.javazen.telegram.bot.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.javazen.telegram.bot.Bot;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.ServletContext;
import java.util.Map;

@Service
public class BotInitializer {
    private static final Logger LOGGER = LoggerFactory.getLogger(BotInitializer.class);
    private Map<String, Bot> bots;
    private String path;

    @Autowired
    public BotInitializer(ServletContext servletContext, @Value("${server.url}")String serverUrl) {
        path = serverUrl + servletContext.getContextPath() + CallbackService.CALLBACK_PATH + "/";
    }

    @Resource(name = "botMap")
    public void setBots(Map<String, Bot> bots) {
        this.bots = bots;
    }

    @PostConstruct
    public void initBots(){
        LOGGER.debug("Start init bots");
        for (Map.Entry<String, Bot> entry: bots.entrySet()){
            initBot(entry.getKey(), entry.getValue());
        }
    }

    private void initBot(String name, Bot bot){
        bot.getService().setWebHook(path + name);
        bot.onStart();
    }
}
