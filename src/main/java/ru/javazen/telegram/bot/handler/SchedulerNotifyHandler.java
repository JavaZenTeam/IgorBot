package ru.javazen.telegram.bot.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.DefaultManagedTaskScheduler;
import ru.javazen.telegram.bot.entity.request.Update;
import ru.javazen.telegram.bot.service.MessageHelper;
import ru.javazen.telegram.bot.service.TelegramService;

import java.util.*;
import java.util.concurrent.ScheduledFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SchedulerNotifyHandler implements UpdateHandler {

    private Map<Long, Integer> userTasks = new HashMap<>();

    private TaskScheduler taskScheduler = new DefaultManagedTaskScheduler();

    private String validationPattern;

    private int daysLimit, tasksLimit;

    @Autowired
    private TelegramService telegramService;

    @Override
    public boolean handle(final Update update,final String token) {
        String message = update.getMessage().getText();

        final long userId = update.getMessage().getFrom().getId();
        final Parameters parameters;

        Pattern pattern = Pattern.compile(validationPattern);
        Matcher matcher = pattern.matcher(message);

        if(matcher.matches()) {
            message = message.substring(0, matcher.end()).trim();
        } else {
            return false;
        }

        try {
            parameters = parseParameters(message);
        } catch (RuntimeException e) {
            return false;
        }

        //userTasks.computeIfAbsent(userId, num -> 0);
        if (!userTasks.containsKey(userId)) {
            userTasks.put(userId, 1);
        }

        if (userTasks.get(userId) > tasksLimit) {
            telegramService.execute(MessageHelper.answerWithReply(update.getMessage(), "Я столько не запомню(("), token);
            return true;
        }

        Calendar calendar = new GregorianCalendar();
        calendar.add(Calendar.DAY_OF_YEAR, daysLimit);
        if (parameters.getDate().compareTo(calendar.getTime()) > 0) {
            telegramService.execute(MessageHelper.answerWithReply(update.getMessage(), "Так долго я помнить не смогу, сорри"), token);
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

    private Parameters parseParameters(String message) {
        String regexp = /* 1 */"(\\d* ?(?:л|лет|г|год|года) )?" +
                        /* 2 */"(\\d* ?(?:м|мес|месяц|месяца|месяцев) )?" +
                        /* 3 */"(\\d* ?(?:н|нед|недель|неделю|недели) )?" +
                        /* 4 */"(\\d* ?(?:д|дн|дней|дня|день) )?" +
                        /* 5 */"(\\d* ?(?:ч|час|часа|часов) )?" +
                        /* 6 */"(\\d* ?(?:м|мин|минуту|минуты|минут) )?" +
                        /* 7 */"(\\d* ?(?:с|сек|секунду|секунды|секунд) )?" +
                        /* 8 */"(.*)";

        int[] timeUnits = {
                0,
                /* 1 */Calendar.YEAR,
                /* 2 */Calendar.MONTH,
                /* 3 */Calendar.WEEK_OF_YEAR,
                /* 4 */Calendar.DAY_OF_YEAR,
                /* 5 */Calendar.HOUR,
                /* 6 */Calendar.MINUTE,
                /* 7 */Calendar.SECOND
        };

        Pattern pattern = Pattern.compile(regexp);
        Matcher matcher = pattern.matcher(message);

        GregorianCalendar calendar = new GregorianCalendar();
        String returnMessage = null;
        Parameters parameters = new Parameters();

        if(matcher.matches()) {
            String time;
            int value;

            for (int i = 1; i < timeUnits.length; i++) {
                if (!matcher.group(i).isEmpty())
                {
                    time = matcher.group(i).replaceAll("\\D", "");
                    if (time != null && !time.isEmpty()) value = Integer.parseInt(time);
                    else value = 1;
                    calendar.add(timeUnits[i], value);
                }
            }
        }

        returnMessage = matcher.group(matcher.groupCount());
        if (returnMessage == null || returnMessage.isEmpty()) {
            throw new IllegalArgumentException("Message is empty");
        }

        parameters.setMessage(returnMessage);
        parameters.setDate(calendar.getTime());

        return parameters;
    }

    public TaskScheduler getTaskScheduler() {
        return taskScheduler;
    }

    public void setTaskScheduler(TaskScheduler taskScheduler) {
        this.taskScheduler = taskScheduler;
    }

    public void setDaysLimit(int daysLimit) {
        this.daysLimit = daysLimit;
    }

    public void setTasksLimit(int tasksLimit)
    {
        this.tasksLimit = tasksLimit;
    }

    public void setValidationPattern(String pattern) {
        this.validationPattern = pattern;
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
