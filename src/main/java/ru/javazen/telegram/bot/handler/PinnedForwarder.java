package ru.javazen.telegram.bot.handler;

import ru.javazen.telegram.bot.Bot;
import ru.javazen.telegram.bot.entity.request.Message;
import ru.javazen.telegram.bot.entity.request.Update;
import ru.javazen.telegram.bot.entity.response.ForwardMessage;

/**
 * Created by egor on 18.05.2016.
 * kto prochital, tot andrey
 */

public class PinnedForwarder implements UpdateHandler {

    private String storeChatId;

    public boolean handle(Update update, Bot bot) {
        Message pinnedMessage = update.getMessage().getPinnedMessage();
        if (pinnedMessage == null) return false;

        ForwardMessage forwardMessage = new ForwardMessage();
        forwardMessage.setFromChatId(Long.toString(update.getMessage().getChat().getId()));
        forwardMessage.setMessageId(update.getMessage().getPinnedMessage().getMessageId());
        forwardMessage.setChatId(storeChatId);
        bot.getService().forwardMessage(forwardMessage);

        return true;
    }

    public String getStoreChatId() {
        return storeChatId;
    }

    public void setStoreChatId(String storeChatId) {
        this.storeChatId = storeChatId;
    }
}
