package ru.javazen.telegram.bot.handler.base;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public interface CallbackQueryHandler extends UpdateHandler {

    @Override
    default boolean handle(Update update, AbsSender sender) throws TelegramApiException {
        return update.hasCallbackQuery() && handle(update.getCallbackQuery(), sender);
    }

    boolean handle(CallbackQuery callbackQuery, AbsSender sender) throws TelegramApiException;
}
