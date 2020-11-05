package ru.javazen.telegram.bot.handler.base;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public interface MessageHandler extends UpdateHandler {
    @Override
    default boolean handle(Update update, AbsSender sender) throws TelegramApiException {
        return update.hasMessage() && handle(update.getMessage(), sender);
    }

    boolean handle(Message message, AbsSender sender) throws TelegramApiException;
}
