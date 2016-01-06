package ru.javazen.telegram.bot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import ru.javazen.telegram.bot.entity.Update;
import ru.javazen.telegram.bot.handler.UpdateHandler;
import ru.javazen.telegram.bot.method.TelegramMethod;
import ru.javazen.telegram.bot.service.TelegramService;

import java.util.Collection;

public class CompositeBot extends Bot {
    private static final Logger LOGGER = LoggerFactory.getLogger(CompositeBot.class);

    @Autowired
    private TelegramService telegramService;

    private Collection<UpdateHandler> updateHandlers;

    public CompositeBot(String token) {
        super(token);
    }

    public Collection<UpdateHandler> getUpdateHandlers() {
        return updateHandlers;
    }

    @Required
    public void setUpdateHandlers(Collection<UpdateHandler> updateHandlers) {
        this.updateHandlers = updateHandlers;
    }

    public void onStart() {
    }

    public void onUpdate(Update update) {
        for (UpdateHandler handler : getUpdateHandlers()){
            TelegramMethod result = handler.handle(update);
            if (result == null) continue;

            boolean success = telegramService.execute(result, getToken());
            if (success){
                LOGGER.debug("Successfully handle update and send something");
            }
            else {
                LOGGER.error("Failed to handle update");
            }
            return;
        }
        LOGGER.debug("This update is not handled: {0}", update);
    }
}
