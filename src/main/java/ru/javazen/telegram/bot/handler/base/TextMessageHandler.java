package ru.javazen.telegram.bot.handler.base;

import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ru.javazen.telegram.bot.util.MessageHelper;

public interface TextMessageHandler extends MessageHandler {
    @Override
    default boolean handle(Message message, AbsSender sender) throws TelegramApiException {
        String text = MessageHelper.getActualText(message);
        return text != null && handle(message, text, sender);
    }

    boolean handle(Message message, String text, AbsSender sender) throws TelegramApiException;
}
