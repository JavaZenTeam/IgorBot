package ru.javazen.telegram.bot.handler;

import ru.javazen.telegram.bot.entity.request.Update;
import ru.javazen.telegram.bot.method.TelegramMethod;
import ru.javazen.telegram.bot.service.MessageHelper;

public class Repeater implements UpdateHandler {
    public TelegramMethod handle(Update update) {
        String text = update.getMessage().getText();
        if (text == null) return null;
        return MessageHelper.answer(update.getMessage(), text);
    }
}
