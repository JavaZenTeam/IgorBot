package ru.javazen.telegram.bot.handler;

import ru.javazen.telegram.bot.BotMethodExecutor;
import ru.javazen.telegram.bot.entity.Update;
import ru.javazen.telegram.bot.method.send.ForwardMessage;

public class ChatBridge implements UpdateHandler {

    private long firstChat;

    private long secondChat;

    @Override
    public boolean handle(Update update, BotMethodExecutor executor) {
        if (update.getMessage().getChat().getId() != firstChat
                && update.getMessage().getChat().getId() != secondChat) return false;

        long chatTo = update.getMessage().getChat().getId() == firstChat ? secondChat : firstChat;

        ForwardMessage forwardMessage = new ForwardMessage();
        forwardMessage.setFromChatId(Long.toString(update.getMessage().getChat().getId()));
        forwardMessage.setMessageId(update.getMessage().getMessageId());

        forwardMessage.setChatId(Long.toString(chatTo));
        executor.execute(forwardMessage, Void.class);

        return false;
    }

    public void setSecondChat(long secondChat) {
        this.secondChat = secondChat;
    }

    public void setFirstChat(long firstChat) {
        this.firstChat = firstChat;
    }
}
