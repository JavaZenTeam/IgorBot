package ru.javazen.telegram.bot.handler;

import ru.javazen.telegram.bot.entity.request.Update;
import ru.javazen.telegram.bot.method.TelegramMethod;
import ru.javazen.telegram.bot.service.MessageHelper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RepeaterAdvanced implements UpdateHandler{
    private static final Pattern DEFAULT_PATTERN = Pattern.compile("/repeat (.*)");
    private Pattern pattern = DEFAULT_PATTERN;

    public void setPattern(String pattern) {
        this.pattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
    }
    public TelegramMethod handle(Update update) {
        String text = update.getMessage().getText();
        if (text == null) return null;

        String answer = solveAnswer(text);
        if (answer == null) return null;

        return MessageHelper.answer(update.getMessage(), answer);
    }

    public String solveAnswer(String text){
        Matcher matcher = pattern.matcher(text);
        if (!matcher.matches() || matcher.groupCount() < 1) return null;
        return matcher.group(1);
    }
}
