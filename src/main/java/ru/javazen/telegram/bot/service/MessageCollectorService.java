package ru.javazen.telegram.bot.service;

import org.telegram.telegrambots.api.objects.Message;

public interface MessageCollectorService {
    void saveMessage(Message userMessage);
    void saveBotUsage(Message userMessage, Message botResponse, String handlerName);
}
