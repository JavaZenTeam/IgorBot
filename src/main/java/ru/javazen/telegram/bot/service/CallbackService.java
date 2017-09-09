package ru.javazen.telegram.bot.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.javazen.telegram.bot.TelegramBot;
import ru.javazen.telegram.bot.entity.Update;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

@Path(CallbackService.CALLBACK_PATH)
@Service
public class CallbackService {
    public static final String CALLBACK_PATH = "/callback";

    private static final Logger LOGGER = LoggerFactory.getLogger(CallbackService.class);

    @Autowired
    private TelegramBot bot;

    @POST
    @Path("/{botName}")
    @Consumes(MediaType.APPLICATION_JSON)
    public void callback(@PathParam("botName") String botName, Update update) {

        LOGGER.debug("Start callback bot {}", botName);
        LOGGER.debug("Update body: {}", update);

        //TODO If the message has been changed, the message is null (from bot API 2.1)
        if (update.getMessage() == null) {
            LOGGER.error("Message in Update is not valid: {}", update);
            return;
        }
        
        if (bot == null) {
            LOGGER.warn("Bot with name={} doesn't exist", botName);
            return;
        }

        bot.handleUpdate(update);
    }
}
