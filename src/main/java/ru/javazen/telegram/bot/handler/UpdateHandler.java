package ru.javazen.telegram.bot.handler;

import ru.javazen.telegram.bot.entity.request.Update;
import ru.javazen.telegram.bot.method.TelegramMethod;

public interface UpdateHandler {
    TelegramMethod handle(Update update);
}
