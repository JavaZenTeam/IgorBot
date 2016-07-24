package ru.javazen.telegram.bot;

import ru.javazen.telegram.bot.entity.request.Update;
import ru.javazen.telegram.bot.service.TelegramBotService;

public abstract class Bot {
    private TelegramBotService service;

    public abstract void onStart();
    public abstract void onUpdate(Update update);

    public Bot(TelegramBotService service) {
        this.service = service;
    }

    public TelegramBotService getService() {
        return service;
    }
}
