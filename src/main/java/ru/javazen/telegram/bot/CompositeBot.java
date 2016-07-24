package ru.javazen.telegram.bot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.javazen.telegram.bot.entity.request.Update;
import ru.javazen.telegram.bot.entity.response.SendMessage;
import ru.javazen.telegram.bot.handler.UpdateHandler;
import ru.javazen.telegram.bot.service.TelegramBotService;

import java.util.ArrayList;
import java.util.Collection;

public class CompositeBot extends Bot {
    private static final Logger LOGGER = LoggerFactory.getLogger(CompositeBot.class);

    private Collection<UpdateHandler> updateHandlers = new ArrayList<>();
    private Long supportChatId;

    public CompositeBot(TelegramBotService service) {
        super(service);
    }

    public void setUpdateHandlers(Collection<UpdateHandler> updateHandlers) {
        this.updateHandlers.clear();
        this.updateHandlers.addAll(updateHandlers);
    }

    public void setSupportChatId(Long supportChatId) {
        this.supportChatId = supportChatId;
    }

    public void onStart() {
        if (supportChatId != null){
            getService().sendMessage(new SendMessage(supportChatId, "Start successfully"));
        }
    }

    public void onUpdate(Update update) {
        try {
            for (UpdateHandler handler : updateHandlers){
                if (handler.handle(update, this)) return;
            }
            LOGGER.debug("This update is not handled: {}", update);
        } catch (Exception e){
            LOGGER.error("Error on handling update", e);
            if (supportChatId != null){
                String message = String.format("*Error!*\n%s\n```%s```", e, e.getStackTrace()[0]);
                SendMessage msg = new SendMessage(supportChatId, message);
                msg.setParseMode(Constants.PARSE_MODE_MARKDOWN);
                getService().sendMessage(msg);
            }
        }
    }
}
