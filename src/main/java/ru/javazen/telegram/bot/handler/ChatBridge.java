package ru.javazen.telegram.bot.handler;

import org.springframework.beans.factory.annotation.Autowired;
import ru.javazen.telegram.bot.Bot;
import ru.javazen.telegram.bot.entity.request.Update;
import ru.javazen.telegram.bot.entity.response.ForwardMessage;
import ru.javazen.telegram.bot.service.TelegramBotService;

public class ChatBridge implements UpdateHandler {

    private long firstChat;

    private long secondChat;

    @Autowired
    private TelegramBotService botService;

    @Override
    public boolean handle(Update update) {

        if (update.getMessage().getChat().getId() != firstChat
                && update.getMessage().getChat().getId() != secondChat) return false;

        long chatTo = update.getMessage().getChat().getId() == firstChat ? secondChat : firstChat;

        ForwardMessage forwardMessage = new ForwardMessage();
        forwardMessage.setFromChatId(Long.toString(update.getMessage().getChat().getId()));
        forwardMessage.setMessageId(update.getMessage().getMessageId());

        forwardMessage.setChatId(Long.toString(chatTo));
        botService.forwardMessage(forwardMessage);

        return false;
    }

    public void setSecondChat(long secondChat) {
        this.secondChat = secondChat;
    }

    public void setFirstChat(long firstChat) {
        this.firstChat = firstChat;
    }
}
