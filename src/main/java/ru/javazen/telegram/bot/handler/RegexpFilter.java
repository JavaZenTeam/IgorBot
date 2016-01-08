package ru.javazen.telegram.bot.handler;

import org.springframework.util.Assert;
import ru.javazen.telegram.bot.entity.request.Update;
import ru.javazen.telegram.bot.method.TelegramMethod;

import java.util.regex.Pattern;

public class RegexpFilter implements UpdateHandler {
    private static final Pattern DEFAULT_PATTERN = Pattern.compile(".*");
    private UpdateHandler innerHandler = new DumbHandler();
    private Pattern pattern = DEFAULT_PATTERN;

    public TelegramMethod handle(Update update) {
        String text = update.getMessage().getText();
        if (text == null) return null;
        if (!pattern.matcher(text).matches()) return null;
        return innerHandler.handle(update);
    }

    public void setPattern(String pattern) {
        this.pattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
    }

    public void setInnerHandler(UpdateHandler innerHandler) {
        Assert.notNull(innerHandler, "innerHandler can not be null");
        this.innerHandler = innerHandler;
    }
}
