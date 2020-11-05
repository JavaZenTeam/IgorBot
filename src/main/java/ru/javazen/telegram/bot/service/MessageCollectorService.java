package ru.javazen.telegram.bot.service;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface MessageCollectorService {
    void saveUpdate(Update update);
    void saveMessage(Message message);
    void saveBotUsage(Update update, Message botResponse, String handlerName);
}
