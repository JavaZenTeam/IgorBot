package ru.javazen.telegram.bot.handler;

import ru.javazen.telegram.bot.BotMethodExecutor;
import ru.javazen.telegram.bot.entity.Update;

public interface UpdateHandler {
    boolean handle(Update update, BotMethodExecutor executor);
}
