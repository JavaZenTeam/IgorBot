package ru.javazen.telegram.bot.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.javazen.telegram.bot.Bot;
import ru.javazen.telegram.bot.entity.Update;

import javax.annotation.Resource;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import java.util.Map;

@Path("/callback")
@Service
public class CallbackService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CallbackService.class);

    @Resource(name = "botMap")
    private Map<String, Bot> botMap;

    @POST
    @Path("/{botName}")
    @Consumes(MediaType.APPLICATION_JSON)
    public void callback(@PathParam("botName") String botName, Update update) {

        LOGGER.debug("Start callback bot {0}", botName);

        Bot bot = botMap.get(botName);
        if (bot == null) {
            LOGGER.warn("Bot with name={0} doesn't exist", botName);
            return;
        }

        bot.onUpdate(update);
    }
}
