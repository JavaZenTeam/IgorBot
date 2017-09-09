package ru.javazen.telegram.bot.handler;

import ru.javazen.telegram.bot.BotMethodExecutor;
import ru.javazen.telegram.bot.entity.Update;
import ru.javazen.telegram.bot.method.send.ForwardMessage;

public class SpyModule implements UpdateHandler {

    private long spyOnChatId;
    private long forwardToChatId;

    @Override
    public boolean handle(Update update, BotMethodExecutor executor) {
        if (update.getMessage().getChat().getId() != spyOnChatId) return false;

        ForwardMessage forwardMessage = new ForwardMessage();
        forwardMessage.setFromChatId(Long.toString(update.getMessage().getChat().getId()));
        forwardMessage.setMessageId(update.getMessage().getMessageId());

        forwardMessage.setChatId(Long.toString(forwardToChatId));
        executor.execute(forwardMessage, Void.class);

        return false;
    }

    public void setForwardToChatId(long forwardToChatId) {
        this.forwardToChatId = forwardToChatId;
    }

    public void setSpyOnChatId(long spyOnChatId) {
        this.spyOnChatId = spyOnChatId;
    }
}
