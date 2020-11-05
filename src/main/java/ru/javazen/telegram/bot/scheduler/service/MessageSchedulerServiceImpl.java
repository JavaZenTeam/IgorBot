package ru.javazen.telegram.bot.scheduler.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.DefaultManagedTaskScheduler;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.javazen.telegram.bot.CompositeBot;
import ru.javazen.telegram.bot.logging.TelegramLogger;
import ru.javazen.telegram.bot.model.MessageTask;
import ru.javazen.telegram.bot.repository.MessageTaskRepository;
import ru.javazen.telegram.bot.util.DateInterval;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;


@Service
@Slf4j
public class MessageSchedulerServiceImpl implements MessageSchedulerService {

    private TaskScheduler taskScheduler = new DefaultManagedTaskScheduler();

    private Map<Long, FutureTask> futureTasks = new HashMap<>();

    private final CompositeBot telegramBot;
    private final MessageTaskRepository messageTaskRepository;
    private final TelegramLogger tgLogger;

    public MessageSchedulerServiceImpl(CompositeBot telegramBot, MessageTaskRepository messageTaskRepository, TelegramLogger tgLogger) {
        this.telegramBot = telegramBot;
        this.messageTaskRepository = messageTaskRepository;
        this.tgLogger = tgLogger;
    }

    @Override
    public void scheduleTask(MessageTask task) {
        task.setBotName(telegramBot.getBotUsername());
        messageTaskRepository.save(task);

        performSchedulingTasks(task, telegramBot);
    }

    @Override
    public boolean cancelTaskByChatAndMessage(Long chatId, Integer messageId) {
        MessageTask task = messageTaskRepository.getTaskByChatIdAndMessageId(chatId, messageId.longValue());

        if (task == null) { return false; }

        FutureTask future = futureTasks.get(task.getId());
        futureTasks.remove(task.getId());

        future.getFuture().cancel(false);

        messageTaskRepository.delete(task);
        return true;
    }

    @Override
    public boolean extendTaskByChatAndMessage(Long chatId, Integer messageId, long additionalTime) {
        MessageTask task = messageTaskRepository.getTaskByChatIdAndMessageId(chatId, messageId.longValue());

        if (task == null) { return false; }

        FutureTask futureTask = futureTasks.get(task.getId());

        futureTask.getFuture().cancel(false);

        task.setTimeOfCompletion(task.getTimeOfCompletion() + additionalTime);
        futureTask.setFuture(getFuture(task));
        messageTaskRepository.save(task);
        return true;
    }

    @PostConstruct
    private void loadTasksFromDatabase() {
        Iterable<MessageTask> tasks = messageTaskRepository.findAll();

        for (MessageTask task : tasks) {
            performSchedulingTasks(task, telegramBot);
        }
    }

    private void performSchedulingTasks(MessageTask task, AbsSender sender) {

        ScheduledFuture future = getFuture(task);

        FutureTask futureTask = new FutureTask();
        futureTask.setTaskId(task.getId());
        futureTask.setFuture(future);
        futureTask.setSender(sender);
        futureTasks.put(task.getId(), futureTask);
    }

    private ScheduledFuture getFuture(MessageTask task) {

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(task.getChatId().toString());
        sendMessage.setReplyToMessageId(task.getReplyMessageId().intValue());
        sendMessage.setText(task.getScheduledText());

        return taskScheduler.schedule(() -> {
            FutureTask futureTask = futureTasks.get(task.getId());
            if (futureTask == null) {
                RuntimeException ex = new RuntimeException("Can't find future for task: " + task.getId());
                tgLogger.log(ex);
                throw ex;
            }

            try {
                futureTask.getSender().execute(sendMessage);
            } catch (RuntimeException e) {
                // for case when reply message was removed. TODO - get cause of send error for detect tis case
                sendMessage.setReplyToMessageId(null);
                try {
                    futureTask.getSender().execute(sendMessage);
                } catch (RuntimeException rex) {
                    log.error("Something is wrong with task with {} id. Error message: {}", task.getId(), rex.getMessage());
                    tgLogger.log(rex);
                    throw rex;
                } catch (TelegramApiException te) {
                    log.error("Can't send message", te);
                    tgLogger.log(e);
                    throw new RuntimeException(te);
                }
            } catch (TelegramApiException e) {
                log.error("Can't send message", e);
                tgLogger.log(e);
                throw new RuntimeException(e);
            }

            Integer repeatCount = task.getRepeatCount();
            if (repeatCount != null && repeatCount != 0) {
                task.setTimeOfCompletion(DateInterval.apply(task.getRepeatInterval(), new Date(task.getTimeOfCompletion())).getTime());
                if (repeatCount > 0) {
                    task.setRepeatCount(repeatCount - 1);
                }
                messageTaskRepository.save(task);
                futureTask.setFuture(getFuture(task));
            } else {
                futureTasks.remove(futureTask.taskId);
                messageTaskRepository.delete(task);
            }
        }, new Date(task.getTimeOfCompletion()));
    }

    @PreDestroy
    public void destroy() {
        for (FutureTask futureTask : futureTasks.values()) {
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
