package ru.javazen.telegram.bot.scheduler.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.TaskScheduler;
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

    private final TaskScheduler taskScheduler;
    private Map<Long, FutureTask> futureTasks = new HashMap<>();

    private final MessageTaskRepository messageTaskRepository;

    private CompositeBot telegramBot;
    private TelegramLogger tgLogger;
    private UserEntityRepository userEntityRepository;
    private ChatConfigService chatConfigService;

    public MessageSchedulerServiceImpl(MessageTaskRepository messageTaskRepository, TaskScheduler taskScheduler) {
        this.messageTaskRepository = messageTaskRepository;
        this.taskScheduler = taskScheduler;
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
        if (task.getReplyMessageId() != null) {
            sendMessage.setReplyToMessageId(task.getReplyMessageId().intValue());
        }
        sendMessage.setText(task.getScheduledText());
        sendMessage.setMessageThreadId(task.getMessageThreadId());

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
                String apiResponse = e.getApiResponse();

                if (apiResponse.contains("replied message not found") || 
                    apiResponse.contains("message to be replied not found")) {
                    // Если сообщение для ответа не найдено, отправляем без реплая
                    sendMessage.setReplyToMessageId(null);
                    String username = findUsernameById(task.getUserId().intValue());
                    username = username == null ? "Незнакомец под номером '" + task.getUserId() + "'" : "@" + username;

                    sendMessage.setText(username +  ", как-то давно (" + formattedDate + ") ты просил меня напомнить: " +
                            sendMessage.getText());
                } else if (apiResponse.contains("group chat was upgraded to a supergroup chat")) {
                    sendMessage.setChatId(e.getParameters().getMigrateToChatId().toString());
                    sendMessage.setText("Когда-то (" + formattedDate + ") вы просили напомнить. Но я не мог с вами " +
                            "связаться. В общем, вот: " + sendMessage.getText());
                } else if (apiResponse.contains("chat not found")) {
                    // Если чат не найден, пытаемся отправить пользователю напрямую
                    sendMessage.setChatId(task.getUserId().toString());
                    sendMessage.setReplyToMessageId(null);
                    sendMessage.setText("Когда-то (" + formattedDate + ") ты завел напоминание. Но чата больше нет. " +
                            "В общем, вот: " + sendMessage.getText());
                } else if (apiResponse.contains("bot was kicked from the group chat")) {
                    String newMessageToSend = "Когда-то (" + formattedDate + ") ты завел напоминание: https://t.me/c/" +
                            sendMessage.getChatId() + "/" + sendMessage.getReplyToMessageId() + ". Но меня удалили " +
                            "из чата. В общем, вот: " + sendMessage.getText();
                    sendMessage.setChatId(task.getUserId().toString());
                    sendMessage.setReplyToMessageId(null);
                    sendMessage.setText(newMessageToSend);
                } else {
                    // Неизвестная ошибка - проверяем, постоянная ли она
                    if (isPermanentError(e)) {
                        log.error("Permanent error sending message for task {}. Removing task.", task.getId(), e);
                        tgLogger.log(e);
                        futureTasks.remove(futureTask.taskId);
                        messageTaskRepository.delete(task);
                        return;
                    } else {
                        // Временная ошибка - перепланируем на 10 минут вперед для повторной попытки
                        log.warn("Temporary error sending message for task {}. Will retry in 10 minutes.", task.getId(), e);
                        tgLogger.log(e);
                        // Перепланируем задачу на 10 минут вперед для повторной попытки
                        task.setTimeOfCompletion(task.getTimeOfCompletion() + 10 * 60 * 1000); // +10 минут
                        messageTaskRepository.save(task);
                        futureTask.setFuture(getFuture(task));
                        return;
                    }
                }
                
                // Повторная попытка отправки с исправленным сообщением
                try {
                    futureTask.getSender().execute(sendMessage);
                } catch (TelegramApiRequestException retryE) {
                    // Если при повторной попытке снова ошибка (например, пользователь заблокировал бота),
                    // проверяем, постоянная ли она
                    if (isPermanentError(retryE)) {
                        log.warn("Failed to send scheduled message to user {} (task {}). Permanent error. Removing task.", 
                                task.getUserId(), task.getId());
                        tgLogger.log(retryE);
                        futureTasks.remove(futureTask.taskId);
                        messageTaskRepository.delete(task);
                        return;
                    } else {
                        // Временная ошибка при повторной попытке - перепланируем на 10 минут вперед
                        log.warn("Temporary error on retry for task {}. Will retry in 10 minutes.", task.getId(), retryE);
                        tgLogger.log(retryE);
                        // Перепланируем задачу на 10 минут вперед для повторной попытки
                        task.setTimeOfCompletion(task.getTimeOfCompletion() + 10 * 60 * 1000); // +10 минут
                        messageTaskRepository.save(task);
                        futureTask.setFuture(getFuture(task));
                        return;
                    }
                } catch (TelegramApiException te) {
                    // Для других ошибок Telegram API - считаем временными, перепланируем на 10 минут
                    log.warn("Telegram API error on retry for task {}. Will retry in 10 minutes.", task.getId(), te);
                    tgLogger.log(te);
                    // Перепланируем задачу на 10 минут вперед для повторной попытки
                    task.setTimeOfCompletion(task.getTimeOfCompletion() + 10 * 60 * 1000); // +10 минут
                    messageTaskRepository.save(task);
                    futureTask.setFuture(getFuture(task));
                    return;
                } catch (RuntimeException rex) {
                    // Runtime исключения могут быть разными - логируем как ошибку
                    // Перепланируем на 10 минут для повторной попытки
                    log.error("Runtime error when retrying to send message for task {}. Will retry in 10 minutes.", task.getId(), rex);
                    tgLogger.log(rex);
                    // Перепланируем задачу на 10 минут вперед для повторной попытки
                    task.setTimeOfCompletion(task.getTimeOfCompletion() + 10 * 60 * 1000); // +10 минут
                    messageTaskRepository.save(task);
                    futureTask.setFuture(getFuture(task));
                    return;
                }
            } catch (TelegramApiException te) {
                // Если это не TelegramApiRequestException, а другой тип TelegramApiException
                // Считаем временной ошибкой, перепланируем на 10 минут
                log.warn("Telegram API error for task {}. Will retry in 10 minutes.", task.getId(), te);
                tgLogger.log(te);
                // Перепланируем задачу на 10 минут вперед для повторной попытки
                task.setTimeOfCompletion(task.getTimeOfCompletion() + 10 * 60 * 1000); // +10 минут
                messageTaskRepository.save(task);
                futureTask.setFuture(getFuture(task));
                return;
            }

            // Если отправка успешна, обрабатываем повторения
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

    /**
     * Определяет, является ли ошибка постоянной (permanent) или временной (temporary).
     * Постоянные ошибки означают, что задача никогда не сможет быть выполнена.
     * Временные ошибки могут быть решены при следующей попытке.
     * 
     * @param e исключение Telegram API
     * @return true если ошибка постоянная, false если временная
     */
    private boolean isPermanentError(TelegramApiRequestException e) {
        String apiResponse = e.getApiResponse();
        if (apiResponse == null) {
            // Если нет ответа API, считаем временной ошибкой
            return false;
        }
        
        String lowerResponse = apiResponse.toLowerCase();
        
        // Постоянные ошибки - чат/пользователь недоступен
        if (lowerResponse.contains("chat not found") ||
            lowerResponse.contains("bot was blocked by the user") ||
            lowerResponse.contains("user is deactivated") ||
            lowerResponse.contains("chat_id is empty") ||
            lowerResponse.contains("can't access the chat") ||
            lowerResponse.contains("not enough rights") ||
            lowerResponse.contains("message is too long")) {
            return true;
        }
        
        // Временные ошибки - проблемы сети, rate limiting, проблемы на стороне Telegram
        if (lowerResponse.contains("too many requests") ||
            e.getErrorCode() == 429 || // Too Many Requests
            e.getErrorCode() >= 500 || // Server errors
            e.getErrorCode() == 408 || // Request Timeout
            lowerResponse.contains("timeout") ||
            lowerResponse.contains("connection") ||
            lowerResponse.contains("network")) {
            return false;
        }
        
        // HTTP 400 Bad Request обычно означает постоянную ошибку (неправильные параметры)
        // но некоторые могут быть временными, поэтому проверяем конкретные сообщения выше
        if (e.getErrorCode() == 400) {
            // Если это известная постоянная ошибка из списка выше - уже обработано
            // Иначе считаем постоянной (неправильные параметры запроса)
            return true;
        }
        
        // По умолчанию для неизвестных ошибок считаем временными,
        // чтобы не потерять задачи из-за неожиданных проблем
        return false;
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
