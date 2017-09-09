package ru.javazen.telegram.bot.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.DefaultManagedTaskScheduler;
import ru.javazen.telegram.bot.BotMethodExecutor;
import ru.javazen.telegram.bot.TelegramBot;
import ru.javazen.telegram.bot.method.send.SendMessage;
import ru.javazen.telegram.bot.model.MessageTask;
import ru.javazen.telegram.bot.repository.MessageTaskRepository;
import ru.javazen.telegram.bot.service.MessageSchedulerService;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.*;
import java.util.concurrent.ScheduledFuture;


public class MessageSchedulerServiceImpl implements MessageSchedulerService {

    private TaskScheduler taskScheduler = new DefaultManagedTaskScheduler();

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageSchedulerServiceImpl.class);

    @Autowired
    private TelegramBot telegramBot;

    @Autowired
    private MessageTaskRepository messageTaskRepository;

    /*private Map<Long, ScheduledFuture> futureMap = new HashMap<>();*/
    private List<FutureTask> futureTasks = new ArrayList<>();

    @Override
    public void scheduleTask(MessageTask task) {
        task.setBotName(telegramBot.getName());
        messageTaskRepository.save(task);

        performSchedulingTasks(task, telegramBot.getBotMethodExecutor());

    }

    @Override
    public void cancelTaskByChatAndMessage(Long chatId, Long messageId) {
        /*MessageTask task = messageTaskRepository.getTaskByChatIdAndMessageId(chatId, messageId);

        ScheduledFuture future = futureMap.get(task.getId());
        futureMap.remove(task.getId());

        future.cancel(false);

        messageTaskRepository.delete(task);*/ //TODO
    }

    @Override
    public void extendTaskByChatAndMessage(Long chatId, Long messageId, long additionalTime) {
        //q   g
/*        MessageTask task = messageTaskRepository.getTaskByChatIdAndMessageId(chatId, messageId);
        ScheduledFuture future = futureMap.get(task.getId());
        futureMap.remove(task.getId());

        future.cancel(false);

        task.setTimeOfCompletion(task.getTimeOfCompletion() + additionalTime);*/ //TODO
    }

    @PostConstruct
    private void loadTasksFromDatabase() {
        Iterable<MessageTask> tasks = messageTaskRepository.findAll();

        for (MessageTask task : tasks) {
            performSchedulingTasks(task, telegramBot.getBotMethodExecutor());
        }
    }

    private void performSchedulingTasks(MessageTask task, BotMethodExecutor executor) {

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(task.getChatId().toString());
        sendMessage.setReplyToMessageId(task.getReplyMessageId().intValue());
        sendMessage.setText(task.getScheduledText());


        ScheduledFuture future = taskScheduler.schedule(() -> {
            FutureTask futureTask = futureTasks.stream().filter(fTask -> fTask.getTaskId().equals(task.getId()))
                    .findFirst().get(); //TODO

            try {
                futureTask.getExecutor().execute(sendMessage, Void.class);
            } catch (RuntimeException e) {
                // for case when reply message was removed. TODO - get cause of send error for detect tis case
                sendMessage.setReplyToMessageId(null);
                try {
                    futureTask.getExecutor().execute(sendMessage, Void.class);
                } catch (RuntimeException rex) {
                    LOGGER.error("Something is wrong with task with {} id. Error message: {}", task.getId(), rex.getMessage());
                    throw rex;
                }
            }

            futureTasks.remove(futureTask);
            messageTaskRepository.delete(task);
        }, new Date(task.getTimeOfCompletion()));


        FutureTask futureTask = new FutureTask();
        futureTask.setTaskId(task.getId());
        futureTask.setFuture(future);
        futureTask.setExecutor(executor);
        futureTasks.add(futureTask);
    }

    @PreDestroy
    public void destroy() {
        for (FutureTask futureTask : futureTasks) {
            futureTask.getFuture().cancel(true);
        }
        /*for (Map.Entry<Long, ScheduledFuture> entry : futureMap.entrySet()) {
            entry.getValue().cancel(true);
        }*/
    }


    private static class FutureTask {
        private Long taskId;
        private ScheduledFuture future;
        private BotMethodExecutor executor;

        public Long getTaskId() {
            return taskId;
        }

        public void setTaskId(Long taskId) {
            this.taskId = taskId;
        }

        public ScheduledFuture getFuture() {
            return future;
        }

        public void setFuture(ScheduledFuture future) {
            this.future = future;
        }

        public BotMethodExecutor getExecutor() {
            return executor;
        }

        public void setExecutor(BotMethodExecutor executor) {
            this.executor = executor;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof FutureTask)) return false;

            FutureTask that = (FutureTask) o;

            return getTaskId() != null ? getTaskId().equals(that.getTaskId()) : that.getTaskId() == null;

        }

        @Override
        public int hashCode() {
            return getTaskId() != null ? getTaskId().hashCode() : 0;
        }
    }
}
