package ru.javazen.telegram.bot.handler;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.javazen.telegram.bot.handler.base.TextMessageHandler;

public class Repeater implements TextMessageHandler {

    @Override
    public boolean handle(Message message, String text, AbsSender sender) throws TelegramApiException {
        SendMessage sendMessage = new SendMessage(message.getChatId().toString(), text);
        sendMessage.setMessageThreadId(message.getMessageThreadId());
        sender.execute(sendMessage);
        return true;
    }
}
