package ru.javazen.telegram.bot.handler;

import org.springframework.beans.factory.annotation.Autowired;
import ru.javazen.telegram.bot.entity.request.Update;
import ru.javazen.telegram.bot.entity.response.ForwardMessage;
import ru.javazen.telegram.bot.method.ForwardMessageMethod;
import ru.javazen.telegram.bot.service.TelegramService;

public class ChatBridge implements UpdateHandler {

    @Autowired
    private TelegramService telegramService;

    private long firstChat;

    private long secondChat;

    @Override
    public boolean handle(Update update, String token) {

        if (update.getMessage().getChat().getId() != firstChat
                && update.getMessage().getChat().getId() != secondChat) return false;

        long chatTo = update.getMessage().getChat().getId() == firstChat ? secondChat : firstChat;

        ForwardMessage forwardMessage = new ForwardMessage();
        forwardMessage.setFromChatId(Long.toString(update.getMessage().getChat().getId()));
        forwardMessage.setMessageId(update.getMessage().getMessageId());

        forwardMessage.setChatId(Long.toString(chatTo));
        telegramService.execute(new ForwardMessageMethod(forwardMessage), token);

        return false;
    }

    public void setSecondChat(long secondChat) {
        this.secondChat = secondChat;
    }

    public void setFirstChat(long firstChat) {
        this.firstChat = firstChat;
    }
}
