package ru.javazen.telegram.bot.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.javazen.telegram.bot.handler.base.TextMessageHandler;

import java.util.Date;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class Counter implements TextMessageHandler {
    private Pattern pattern;
    private String errorMessage;
    private TaskScheduler taskScheduler;

    @Override
    public boolean handle(Message message, String text, AbsSender sender) throws TelegramApiException {
        Matcher matcher = pattern.matcher(text);
        if (!matcher.matches()) return false;

        Integer from = getParam(matcher, "from", 1);
        Integer to = getParam(matcher, "to", 0);

        if (from == null || to == null) {
            sender.execute(new SendMessage(message.getChatId().toString(), errorMessage));
            return false;
        }

        IntStream.range(Math.min(from, to), Math.max(from, to) + 1)
                .forEach(i -> {
                    SendMessage send = new SendMessage(message.getChatId().toString(), String.valueOf(i));
                    Date time = new Date(Math.abs(i - from) * 1000 + System.currentTimeMillis());
                    taskScheduler.schedule(() -> {
                        try {
                            sender.execute(send);
                        } catch (TelegramApiException e) {
                            throw new RuntimeException(e);
                        }
                    }, time);
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

    @Autowired(required = true)
    public void setPattern(String pattern) {
        this.pattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.DOTALL);
    }

    @Autowired(required = true)
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Autowired
    public void setTaskScheduler(TaskScheduler taskScheduler) {
        this.taskScheduler = taskScheduler;
    }
}
