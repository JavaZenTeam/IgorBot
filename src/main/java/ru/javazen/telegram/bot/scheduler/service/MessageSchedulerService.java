package ru.javazen.telegram.bot.scheduler.service;

import ru.javazen.telegram.bot.model.MessageTask;

public interface MessageSchedulerService {

    void scheduleTask(MessageTask messageTask);

    boolean cancelTaskByChatAndMessage(Long chatId, Integer messageId);

    boolean extendTaskByChatAndMessage(Long chatId, Integer messageId, long additionalTime);
}
