package ru.javazen.telegram.bot.scheduler.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.DefaultManagedTaskScheduler;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ru.javazen.telegram.bot.CompositeBot;
import ru.javazen.telegram.bot.model.MessageTask;
import ru.javazen.telegram.bot.repository.MessageTaskRepository;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ScheduledFuture;


public class MessageSchedulerServiceImpl implements MessageSchedulerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageSchedulerServiceImpl.class);

    private TaskScheduler taskScheduler = new DefaultManagedTaskScheduler();

    private List<FutureTask> futureTasks = new ArrayList<>();

    private final CompositeBot telegramBot;
    private final MessageTaskRepository messageTaskRepository;

    public MessageSchedulerServiceImpl(CompositeBot telegramBot, MessageTaskRepository messageTaskRepository) {
        this.telegramBot = telegramBot;
        this.messageTaskRepository = messageTaskRepository;
    }

    @Override
    public void scheduleTask(MessageTask task) {
        task.setBotName(telegramBot.getBotUsername());
        messageTaskRepository.save(task);

        performSchedulingTasks(task, telegramBot);

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
        /*MessageTask task = messageTaskRepository.getTaskByChatIdAndMessageId(chatId, messageId);
        ScheduledFuture future = futureMap.get(task.getId());
        futureMap.remove(task.getId());

        future.cancel(false);

        task.setTimeOfCompletion(task.getTimeOfCompletion() + additionalTime);*/ //TODO
    }

    @PostConstruct
    private void loadTasksFromDatabase() {
        Iterable<MessageTask> tasks = messageTaskRepository.findAll();

        for (MessageTask task : tasks) {
            performSchedulingTasks(task, telegramBot);
        }
    }

    private void performSchedulingTasks(MessageTask task, AbsSender sender) {

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(task.getChatId().toString());
        sendMessage.setReplyToMessageId(task.getReplyMessageId().intValue());
        sendMessage.setText(task.getScheduledText());


        ScheduledFuture future = taskScheduler.schedule(() -> {
            FutureTask futureTask = futureTasks.stream().filter(fTask -> fTask.getTaskId().equals(task.getId()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Can't find future for task: " + task.getId()));

            try {
                futureTask.getSender().execute(sendMessage);
            } catch (RuntimeException e) {
                // for case when reply message was removed. TODO - get cause of send error for detect tis case
                sendMessage.setReplyToMessageId(null);
                try {
                    futureTask.getSender().execute(sendMessage);
                } catch (RuntimeException rex) {
                    LOGGER.error("Something is wrong with task with {} id. Error message: {}", task.getId(), rex.getMessage());
                    throw rex;
                } catch (TelegramApiException te) {
                    LOGGER.error("Can't send message", te);
                    throw new RuntimeException(te);
                }
            } catch (TelegramApiException e) {
                LOGGER.error("Can't send message", e);
                throw new RuntimeException(e);
            }

            futureTasks.remove(futureTask);
            messageTaskRepository.delete(task);
        }, new Date(task.getTimeOfCompletion().toInstant().toEpochMilli()));


        FutureTask futureTask = new FutureTask();
        futureTask.setTaskId(task.getId());
        futureTask.setFuture(future);
        futureTask.setSender(sender);
        futureTasks.add(futureTask);
    }

    @PreDestroy
    public void destroy() {
        for (FutureTask futureTask : futureTasks) {
            futureTask.getFuture().cancel(true);
        }
    }


    private static class FutureTask {
        private Long taskId;
        private ScheduledFuture future;
        private AbsSender sender;

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

        public AbsSender getSender() {
            return sender;
        }

        public void setSender(AbsSender sender) {
            this.sender = sender;
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
