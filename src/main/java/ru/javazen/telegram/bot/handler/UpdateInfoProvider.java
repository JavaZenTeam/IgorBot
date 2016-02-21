package ru.javazen.telegram.bot.handler;

import ru.javazen.telegram.bot.entity.request.Update;
import ru.javazen.telegram.bot.method.TelegramMethod;
import ru.javazen.telegram.bot.service.MessageHelper;

public class UpdateInfoProvider implements UpdateHandler {

    public TelegramMethod handle(Update update) {
        return MessageHelper.answer(update.getMessage(), update.toString());
    }
}
