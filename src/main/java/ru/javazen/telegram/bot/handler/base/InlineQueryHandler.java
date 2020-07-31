package ru.javazen.telegram.bot.handler.base;

import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.inlinequery.InlineQuery;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public interface InlineQueryHandler extends UpdateHandler {

    @Override
    default boolean handle(Update update, AbsSender sender) throws TelegramApiException {
        return update.hasInlineQuery() && handle(update.getInlineQuery(), sender);
    }

    boolean handle(InlineQuery inlineQuery, AbsSender sender) throws TelegramApiException;
}
