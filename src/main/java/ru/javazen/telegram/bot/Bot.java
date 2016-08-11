package ru.javazen.telegram.bot;

import ru.javazen.telegram.bot.entity.request.Update;
import ru.javazen.telegram.bot.service.TelegramBotService;

public interface Bot {
    void onStart();
    void onUpdate(Update update);
    TelegramBotService getService();
}
