package ru.javazen.telegram.bot.handler;

import ru.javazen.telegram.bot.entity.request.Update;
import ru.javazen.telegram.bot.method.TelegramMethod;

public class DumbHandler implements UpdateHandler {

    @Override
    public TelegramMethod handle(Update update) {
        return null;
    }
}
