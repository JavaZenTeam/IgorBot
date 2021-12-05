package ru.javazen.telegram.bot.handler;


import org.telegram.telegrambots.meta.api.methods.ForwardMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.javazen.telegram.bot.handler.base.MessageHandler;

public class ChatBridge implements MessageHandler {

    private long firstChat;

    private long secondChat;

    @Override
    public boolean handle(Message message, AbsSender sender) throws TelegramApiException {
        if (message.getChatId() != firstChat && message.getChatId() != secondChat) return false;

        Long chatTo = message.getChatId() == firstChat ? secondChat : firstChat;

        ForwardMessage forwardMessage = new ForwardMessage();
        forwardMessage.setFromChatId(message.getChatId().toString());
        forwardMessage.setMessageId(message.getMessageId());
        forwardMessage.setChatId(chatTo.toString());
        sender.execute(forwardMessage);

        return false;
    }

    public void setSecondChat(long secondChat) {
        this.secondChat = secondChat;
    }

    public void setFirstChat(long firstChat) {
        this.firstChat = firstChat;
    }
}
