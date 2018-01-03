package ru.javazen.telegram.bot.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.scheduling.TaskScheduler;
import ru.javazen.telegram.bot.BotMethodExecutor;
import ru.javazen.telegram.bot.entity.Update;
import ru.javazen.telegram.bot.method.send.SendMessage;
import ru.javazen.telegram.bot.util.MessageHelper;

import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class Counter implements UpdateHandler {
    private Pattern pattern;
    private String errorMessage;
    private TaskScheduler taskScheduler;

    @Override
    public boolean handle(Update update, BotMethodExecutor executor) {
        String text = MessageHelper.getActualText(update.getMessage());
        if (text == null) return false;

        Matcher matcher = pattern.matcher(text);
        if (!matcher.matches()) return false;

        Integer from = getParam(matcher, "from", 1);
        Integer to = getParam(matcher, "to", 0);

        if (from == null || to == null){
            executor.execute(MessageHelper.answer(update.getMessage(), errorMessage), Void.class);
            return false;
        }

        IntStream.range(Math.min(from, to), Math.max(from, to) + 1)
                .forEach(i -> {
                    SendMessage message = MessageHelper.answer(update.getMessage(), String.valueOf(i));
                    Date time = new Date(Math.abs(i - from) * 1000 + System.currentTimeMillis());
                    taskScheduler.schedule(() -> executor.execute(message, Void.class), time);
                });

        return true;
    }

    private Integer getParam(Matcher matcher, String paramName, int defaultValue) {
        Integer result = Optional.ofNullable(matcher.group(paramName)).map(Integer::parseInt).orElse(defaultValue);
        if (result < 0 || result > 10) {
            return null;
        }
        return result;
    }

    @Required
    public void setPattern(String pattern) {
        this.pattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.DOTALL);
    }

    @Required
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Autowired
    public void setTaskScheduler(TaskScheduler taskScheduler) {
        this.taskScheduler = taskScheduler;
    }
}
