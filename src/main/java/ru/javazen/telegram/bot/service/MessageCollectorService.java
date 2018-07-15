package ru.javazen.telegram.bot.service;

import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;

public interface MessageCollectorService {
    void saveUpdate(Update update);

    void saveBotUsage(Update update, Message botResponse, String handlerName);
}
