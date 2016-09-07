package ru.javazen.telegram.bot.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.DefaultManagedTaskScheduler;
import ru.javazen.telegram.bot.entity.response.SendMessage;
import ru.javazen.telegram.bot.model.MessageTask;
import ru.javazen.telegram.bot.repository.MessageTaskRepository;
import ru.javazen.telegram.bot.service.MessageSchedulerService;
import ru.javazen.telegram.bot.service.TelegramBotService;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

import static ru.javazen.telegram.bot.service.MessageHelper.answer;


public class MessageSchedulerServiceImpl implements MessageSchedulerService {

    private TaskScheduler taskScheduler = new DefaultManagedTaskScheduler();

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
            botService.sendMessage(sendMessage);

            futureMap.remove(task.getId());
            messageTaskRepository.delete(task);
        }, new Date(task.getTimeOfCompletion()));

        futureMap.put(task.getId(), future);
    }
}
