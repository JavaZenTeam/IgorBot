package ru.javazen.telegram.bot.handler;

import ru.javazen.telegram.bot.Bot;
import ru.javazen.telegram.bot.entity.request.Update;

public interface UpdateHandler {
    boolean handle(Update update);
}
