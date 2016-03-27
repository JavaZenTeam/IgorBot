package ru.javazen.telegram.bot.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.DefaultManagedTaskScheduler;
import ru.javazen.telegram.bot.entity.request.Update;
import ru.javazen.telegram.bot.service.MessageHelper;
import ru.javazen.telegram.bot.service.TelegramService;

import java.util.*;
import java.util.concurrent.ScheduledFuture;

public class SchedulerNotifyHandler implements UpdateHandler {

    private Map<Long, Integer> userTasks = new HashMap<>();

    private TaskScheduler taskScheduler = new DefaultManagedTaskScheduler();

    @Autowired
    private TelegramService telegramService;

    //TODO !!!!
    @Override
    public boolean handle(final Update update,final String token) {
        final long userId = update.getMessage().getFrom().getId();

        //userTasks.computeIfAbsent(userId, num -> 0);
        if (!userTasks.containsKey(userId)) {
            userTasks.put(userId, 0);
        }

        if (userTasks.get(userId) > 10) {
            telegramService.execute(MessageHelper.answerWithReply(update.getMessage(), "Превышено количество тасок"), token);
            return true;
        }
        final Parameters parameters;
        try {
            parameters = parseParameters(update.getMessage().getText());
        } catch (RuntimeException e) {
            return false;
        }

        if (parameters.getDate().compareTo(new Date(new Date().getTime() + 1000 * 60 * 60 * 24 + 1)) > 0) {
            telegramService.execute(MessageHelper.answerWithReply(update.getMessage(), "Запрещено устанавливать таску более чем на сутки"), token);
            return true;
        }
        //userTasks.computeIfPresent(userId, (key, val) -> val+1);
        userTasks.put(userId, userTasks.get(userId) + 1);

        //todo j8
        taskScheduler.schedule(new Runnable() {
            @Override
            public void run() {
                //userTasks.computeIfPresent(userId, (key, val) -> val-1);
                userTasks.put(userId, userTasks.get(userId) - 1);
                telegramService.execute(MessageHelper.answerWithReply(update.getMessage(), parameters.getMessage()), token);
        }}, parameters.getDate());

        return true;
    }

    //TODO
    private Parameters parseParameters(String message) {
        String tail = message.substring(message.indexOf("через") + "через".length());
        long delayAsMillis = 0;
        String returnMessage = null;
        Parameters parameters = new Parameters();

        List<String> units = Arrays.asList("сек", "мин", "час");
        for(String unit : units) {
            if (tail.contains(unit)) {
                returnMessage = tail.substring(tail.indexOf(unit) + unit.length());

                String time = tail.substring(0, tail.indexOf(unit));

                switch (unit) {
                    case "сек":
                        delayAsMillis = Long.parseLong(time.trim()) * 1000;
                        break;
                    case "мин":
                        delayAsMillis = Long.parseLong(time.trim()) * 1000 * 60;
                        break;
                    case "час":
                        delayAsMillis = Long.parseLong(time.trim()) * 1000 * 60 * 60;
                        break;
                }
            }
        }

        parameters.setMessage(returnMessage);
        parameters.setDate(new Date(new Date().getTime() + delayAsMillis));

        return parameters;
    }

    public TaskScheduler getTaskScheduler() {
        return taskScheduler;
    }

    public void setTaskScheduler(TaskScheduler taskScheduler) {
        this.taskScheduler = taskScheduler;
    }

    private static class Parameters {
        private String message;

        private Date date;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }
    }
}
