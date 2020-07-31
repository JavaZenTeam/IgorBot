package ru.javazen.telegram.bot.handler.base;

import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public interface UpdateHandler {
    boolean handle(Update update, AbsSender sender) throws TelegramApiException;

    default String getName() {
        return getClass().getSimpleName();
    }
}
