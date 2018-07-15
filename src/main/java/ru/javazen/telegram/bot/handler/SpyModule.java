package ru.javazen.telegram.bot.handler;


import org.telegram.telegrambots.api.methods.ForwardMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ru.javazen.telegram.bot.handler.base.MessageHandler;

public class SpyModule implements MessageHandler {

    private long spyOnChatId;
    private long forwardToChatId;

    @Override
    public boolean handle(Message message, AbsSender sender) throws TelegramApiException {
        if (message.getChatId() != spyOnChatId) return false;

        ForwardMessage forwardMessage = new ForwardMessage();
        forwardMessage.setFromChatId(message.getChatId());
        forwardMessage.setMessageId(message.getMessageId());

        forwardMessage.setChatId(Long.toString(forwardToChatId));
        sender.execute(forwardMessage);

        return false;
    }

    public void setForwardToChatId(long forwardToChatId) {
        this.forwardToChatId = forwardToChatId;
    }

    public void setSpyOnChatId(long spyOnChatId) {
        this.spyOnChatId = spyOnChatId;
    }
}
