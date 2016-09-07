package ru.javazen.telegram.bot.service;

import ru.javazen.telegram.bot.model.MessageTask;

public interface MessageSchedulerService {

    void scheduleTask(MessageTask messageTask);

    void cancelTaskByChatAndMessage(Long chatId, Long messageId);

    void extendTaskByChatAndMessage(Long chatId, Long messageId, long additionalTime);
}
