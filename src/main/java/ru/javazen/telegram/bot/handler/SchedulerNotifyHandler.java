package ru.javazen.telegram.bot.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.DefaultManagedTaskScheduler;
import ru.javazen.telegram.bot.entity.request.Update;
import ru.javazen.telegram.bot.model.MessageTask;
import ru.javazen.telegram.bot.service.MessageHelper;
import ru.javazen.telegram.bot.service.MessageSchedulerService;
import ru.javazen.telegram.bot.service.TelegramBotService;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ru.javazen.telegram.bot.service.MessageHelper.answer;

public class SchedulerNotifyHandler implements UpdateHandler {

    @Autowired
    private TelegramBotService botService;

    @Autowired
    private MessageSchedulerService messageSchedulerService;

    private TaskScheduler taskScheduler = new DefaultManagedTaskScheduler();

    private String validationPattern;

    private int daysLimit, tasksLimit;
    private String successResponse;
    private String defaultReturnMessage;

    @Override
    public boolean handle(final Update update) {
        String message = MessageHelper.getActualText(update.getMessage());
        if (message == null || message.isEmpty()) {
            return false;
        }

        final long userId = update.getMessage().getFrom().getId();
        final Parameters parameters;

        Pattern pattern = Pattern.compile(validationPattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.DOTALL);
        Matcher matcher = pattern.matcher(message);

        if (matcher.matches()) {
            message = matcher.group(1);
        } else {
            return false;
        }

        try {
            parameters = parseParameters(message);
        } catch (RuntimeException e) {
            return false;
        }

        // todo - task limit by time

        Calendar calendar = new GregorianCalendar();
        calendar.add(Calendar.DAY_OF_YEAR, daysLimit);
        if (parameters.getDate().compareTo(calendar.getTime()) > 0) {
            botService.sendMessage(answer(update.getMessage(), "Так долго я помнить не смогу, сорри", true));
            return true;
        }

        /*userTasks.computeIfPresent(userId, (key, val) -> val + 1);*/
        botService.sendMessage(answer(update.getMessage(), successResponse));

        MessageTask task = new MessageTask();
        task.setChatId(update.getMessage().getChat().getId());
        task.setMessageId(update.getMessage().getMessageId());
        task.setUserId(userId);
        task.setReplyMessageId(update.getMessage().getMessageId());
        task.setScheduledText(parameters.getMessage());
        task.setTimeOfCompletion(parameters.getDate().getTime());

        messageSchedulerService.scheduleTask(task);


        return true;
    }

    private Parameters parseParameters(String message) {
        String regexp = /* 1 */"( \\d* ?(?:л|лет|г|год|года))?" +
                        /* 2 */"( \\d* ?(?:мес|месяц|месяца|месяцев))?" +
                        /* 3 */"( \\d* ?(?:н|нед|недель|неделю|недели))?" +
                        /* 4 */"( \\d* ?(?:д|дн|дней|дня|день))?" +
                        /* 5 */"( \\d* ?(?:ч|час|часа|часов))?" +
                        /* 6 */"( \\d* ?(?:м|мин|минуту|минуты|минут))?" +
                        /* 7 */"( \\d* ?(?:с|сек|секунду|секунды|секунд))?" +
                        /* 8 */"( .*)";

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

        Pattern pattern = Pattern.compile(regexp, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
        Matcher matcher = pattern.matcher(message);

        GregorianCalendar calendar = new GregorianCalendar();
        boolean calendarChanged = false;
        Parameters parameters = new Parameters();

        if(matcher.matches()) {
            String time;
            int value;

            for (int i = 1; i < timeUnits.length; i++) {
                if (matcher.group(i) != null && !matcher.group(i).isEmpty())
                {
                    time = matcher.group(i).replaceAll("\\D", "");
                    if (time != null && !time.isEmpty()) value = Integer.parseInt(time);
                    else value = 1;
                    calendarChanged = true;
                    calendar.add(timeUnits[i], value);
                }
            }
        }

        String returnMessage = matcher.group(matcher.groupCount());
        if (returnMessage == null || returnMessage.isEmpty()) {
            returnMessage = defaultReturnMessage;
        }

        if (!calendarChanged) {
            throw new IllegalArgumentException("No time span specified");
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

    public void setSuccessResponse(String successResponse) {
        this.successResponse = successResponse;
    }

    public void setDefaultReturnMessage(String defaultReturnMessage) {
        this.defaultReturnMessage = defaultReturnMessage;
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
