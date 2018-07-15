package ru.javazen.telegram.bot.handler;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ru.javazen.telegram.bot.handler.base.TextMessageHandler;

public class Repeater implements TextMessageHandler {

    @Override
    public boolean handle(Message message, String text, AbsSender sender) throws TelegramApiException {
        sender.execute(new SendMessage(message.getChatId(), text));
        return true;
    }
}
