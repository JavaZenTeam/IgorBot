package ru.javazen.telegram.bot.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.DefaultManagedTaskScheduler;
import ru.javazen.telegram.bot.entity.response.SendMessage;
import ru.javazen.telegram.bot.model.MessageTask;
import ru.javazen.telegram.bot.repository.MessageTaskRepository;
import ru.javazen.telegram.bot.service.MessageSchedulerService;
import ru.javazen.telegram.bot.service.TelegramBotService;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

import static ru.javazen.telegram.bot.service.MessageHelper.answer;


public class MessageSchedulerServiceImpl implements MessageSchedulerService {

    private TaskScheduler taskScheduler = new DefaultManagedTaskScheduler();

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageSchedulerServiceImpl.class);

    @Autowired
    private TelegramBotService botService;

    @Autowired
    private MessageTaskRepository messageTaskRepository;

    private Map<Long, ScheduledFuture> futureMap = new HashMap<>();

    @Override
    public void scheduleTask(MessageTask task) {

        messageTaskRepository.save(task);

        performSchedulingTasks(task);

    }

    @Override
    public void cancelTaskByChatAndMessage(Long chatId, Long messageId) {
        MessageTask task = messageTaskRepository.getTaskByChatIdAndMessageId(chatId, messageId);

        ScheduledFuture future = futureMap.get(task.getId());
        futureMap.remove(task.getId());

        future.cancel(false);

        messageTaskRepository.delete(task);
    }

    @Override
    public void extendTaskByChatAndMessage(Long chatId, Long messageId, long additionalTime) {
        //q   g
        MessageTask task = messageTaskRepository.getTaskByChatIdAndMessageId(chatId, messageId);
        ScheduledFuture future = futureMap.get(task.getId());
        futureMap.remove(task.getId());

        future.cancel(false);

        task.setTimeOfCompletion(task.getTimeOfCompletion() + additionalTime);
    }

    @PostConstruct
    private void loadTasksFromDatabase() {
        Iterable<MessageTask> tasks = messageTaskRepository.findAll();

        for (MessageTask task : tasks) {
            performSchedulingTasks(task);
        }
    }

    private void performSchedulingTasks(MessageTask task) {

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(task.getChatId());
        sendMessage.setReplyMessageId(task.getReplyMessageId());
        sendMessage.setText(task.getScheduledText());

        ScheduledFuture future = taskScheduler.schedule(() -> {
            try {
                botService.sendMessage(sendMessage);
            } catch (RuntimeException e) {
                // for case when reply message was removed. TODO - get cause of send error for detect tis case
                sendMessage.setReplyMessageId(null);
                try {
                    botService.sendMessage(sendMessage);
                } catch (RuntimeException rex) {
                    LOGGER.error("Something is wrong with task with {} id. Error message: {}", task.getId(), rex.getMessage());
                    throw rex;
                }
            }

            futureMap.remove(task.getId());
            messageTaskRepository.delete(task);
        }, new Date(task.getTimeOfCompletion()));

        futureMap.put(task.getId(), future);
    }

    @PreDestroy
    public void destroy() {
        for (Map.Entry<Long, ScheduledFuture> entry : futureMap.entrySet()) {
            entry.getValue().cancel(true);
        }
    }
}
