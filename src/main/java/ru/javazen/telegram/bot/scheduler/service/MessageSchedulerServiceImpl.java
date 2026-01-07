package ru.javazen.telegram.bot.scheduler.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.DefaultManagedTaskScheduler;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import ru.javazen.telegram.bot.CompositeBot;
import ru.javazen.telegram.bot.logging.TelegramLogger;
import ru.javazen.telegram.bot.model.MessageTask;
import ru.javazen.telegram.bot.model.UserEntity;
import ru.javazen.telegram.bot.repository.MessageTaskRepository;
import ru.javazen.telegram.bot.repository.UserEntityRepository;
import ru.javazen.telegram.bot.service.ChatConfigService;
import ru.javazen.telegram.bot.util.DateInterval;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ScheduledFuture;

import static ru.javazen.telegram.bot.scheduler.SchedulerNotifyHandler.TIMEZONE_OFFSET_CONFIG_KEY;


@Service
@Slf4j
public class MessageSchedulerServiceImpl implements MessageSchedulerService {

    private TaskScheduler taskScheduler = new DefaultManagedTaskScheduler();

    private Map<Long, FutureTask> futureTasks = new HashMap<>();

    private final MessageTaskRepository messageTaskRepository;

    private CompositeBot telegramBot;
    private TelegramLogger tgLogger;
    private UserEntityRepository userEntityRepository;
    private ChatConfigService chatConfigService;

    public MessageSchedulerServiceImpl(MessageTaskRepository messageTaskRepository) {
        this.messageTaskRepository = messageTaskRepository;
    }

    @Autowired
    @Lazy
    public void setTelegramBot(CompositeBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    @Autowired
    public void setTgLogger(TelegramLogger tgLogger) {
        this.tgLogger = tgLogger;
    }

    @Autowired
    public void setUserEntityRepository(UserEntityRepository userEntityRepository) {
        this.userEntityRepository = userEntityRepository;
    }

    @Autowired
    public void setChatConfigService(ChatConfigService chatConfigService) {
        this.chatConfigService = chatConfigService;
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
            } catch (TelegramApiRequestException e) {
                // for case when reply message was removed. TODO - get cause of send error for detect tis case
                DateFormat format = new SimpleDateFormat("HH:mm dd.MM.yy");
                TimeZone timeZone = TimeZone.getTimeZone("GMT" + chatConfigService.getProperty(
                        task.getChatId(),
                        TIMEZONE_OFFSET_CONFIG_KEY).orElse("+04:00"));

                format.setTimeZone(timeZone);

                String formattedDate = format.format(new Date(task.getTimeOfCompletion()));

                if (e.getApiResponse().contains("replied message not found")) {
                    sendMessage.setReplyToMessageId(null);
                    String username = findUsernameById(task.getUserId().intValue());
                    username = username == null ? "Незнакомец под номером '" + task.getUserId() + "'" : "@" + username;

                    sendMessage.setText(username +  ", как-то давно (" + formattedDate + ") ты просил меня напомнить: " +
                            sendMessage.getText());
                } else if (e.getApiResponse().contains("group chat was upgraded to a supergroup chat")) {
                    sendMessage.setChatId(e.getParameters().getMigrateToChatId().toString());
                    sendMessage.setText("Когда-то (" + formattedDate + ") вы просили напомнить. Но я не мог с вами " +
                            "связаться. В общем, вот: " + sendMessage.getText());
                } else if (e.getApiResponse().contains("chat not found")) {
                    sendMessage.setChatId(task.getUserId().toString());
                    sendMessage.setReplyToMessageId(null);
                    sendMessage.setText("Когда-то (" + formattedDate + ") ты завел напоминание. Но чата больше нет. " +
                            "В общем, вот: " + sendMessage.getText());
                } else if (e.getApiResponse().contains("bot was kicked from the group chat")) {
                    String newMessageToSend = "Когда-то (" + formattedDate + ") ты завел напоминание: https://t.me/c/" +
                            sendMessage.getChatId() + "/" + sendMessage.getReplyToMessageId() + ". Но меня удалили " +
                            "из чата. В общем, вот: " + sendMessage.getText();
                    sendMessage.setChatId(task.getUserId().toString());
                    sendMessage.setReplyToMessageId(null);
                    sendMessage.setText(newMessageToSend);
                } else {
                    log.error("Can't send message", e);
                    tgLogger.log(e);
                    throw new RuntimeException(e);
                }
                try {
                    futureTask.getSender().execute(sendMessage);
                } catch (RuntimeException rex) {
                    log.error("Something is wrong with task with {} id. Error message: {}", task.getId(), rex.getMessage());
                    tgLogger.log(rex);
                    throw rex;
                } catch (TelegramApiException te) {
                    log.error("Can't send message", te);
                    tgLogger.log(te);
                    throw new RuntimeException(te);
                }
            } catch (TelegramApiException te) {
                log.error("Can't send message", te);
                tgLogger.log(te);
                throw new RuntimeException(te);
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

    private String findUsernameById(Integer id) {
        UserEntity userEntity = userEntityRepository.findByUserId(id);
        if (userEntity != null) {
            return userEntity.getUsername();
        }
        return null;
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
