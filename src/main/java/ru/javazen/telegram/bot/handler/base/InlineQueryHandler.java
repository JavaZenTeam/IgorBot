package ru.javazen.telegram.bot.handler.base;

import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.inlinequery.InlineQuery;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;

public interface InlineQueryHandler extends UpdateHandler {

    @Override
    default boolean handle(Update update, AbsSender sender) throws TelegramApiException {
        return update.hasInlineQuery() && handle(update.getInlineQuery(), sender);
    }

    boolean handle(InlineQuery inlineQuery, AbsSender sender) throws TelegramApiException;
}
