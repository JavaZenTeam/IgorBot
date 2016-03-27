package ru.javazen.telegram.bot.handler;

import org.springframework.beans.factory.annotation.Autowired;
import ru.javazen.telegram.bot.entity.request.Update;
import ru.javazen.telegram.bot.entity.response.ForwardMessage;
import ru.javazen.telegram.bot.method.ForwardMessageMethod;
import ru.javazen.telegram.bot.service.TelegramService;

public class SpyModule implements UpdateHandler {

    @Autowired
    private TelegramService telegramService;

    private long spyOnChatId;
    private long forwardToChatId;

    @Override
    public boolean handle(Update update, String token) {

        if (update.getMessage().getChat().getId() != spyOnChatId) return false;

        ForwardMessage forwardMessage = new ForwardMessage();
        forwardMessage.setFromChatId(Long.toString(update.getMessage().getChat().getId()));
        forwardMessage.setMessageId(update.getMessage().getMessageId());

        forwardMessage.setChatId(Long.toString(forwardToChatId));
        telegramService.execute(new ForwardMessageMethod(forwardMessage), token);

        return false;
    }

    public void setForwardToChatId(long forwardToChatId) {
        this.forwardToChatId = forwardToChatId;
    }

    public void setSpyOnChatId(long spyOnChatId) {
        this.spyOnChatId = spyOnChatId;
    }
}
