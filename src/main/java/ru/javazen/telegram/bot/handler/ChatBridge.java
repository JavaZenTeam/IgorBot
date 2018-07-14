package ru.javazen.telegram.bot.handler;


import org.telegram.telegrambots.api.methods.ForwardMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ru.javazen.telegram.bot.handling.annotation.Handling;
import ru.javazen.telegram.bot.handling.annotation.UpdateType;

@Handling(UpdateType.MESSAGE)
public class ChatBridge implements UpdateHandler {

    private long firstChat;

    private long secondChat;

    @Override
    public boolean handle(Update update, AbsSender sender) throws TelegramApiException {
        if (update.getMessage().getChat().getId() != firstChat
                && update.getMessage().getChat().getId() != secondChat) return false;

        long chatTo = update.getMessage().getChat().getId() == firstChat ? secondChat : firstChat;

        ForwardMessage forwardMessage = new ForwardMessage();
        forwardMessage.setFromChatId(Long.toString(update.getMessage().getChat().getId()));
        forwardMessage.setMessageId(update.getMessage().getMessageId());

        forwardMessage.setChatId(Long.toString(chatTo));
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
