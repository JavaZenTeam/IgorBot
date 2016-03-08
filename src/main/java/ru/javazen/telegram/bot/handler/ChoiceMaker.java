package ru.javazen.telegram.bot.handler;

import ru.javazen.telegram.bot.entity.request.Update;
import ru.javazen.telegram.bot.method.TelegramMethod;
import ru.javazen.telegram.bot.service.MessageHelper;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChoiceMaker implements UpdateHandler{
    private static final Pattern DEFAULT_PATTERN = Pattern.compile("(.+) or (.+)\\?");
    private Pattern pattern = DEFAULT_PATTERN;
    private Comparator<String> comparator;


    public void setPattern(String pattern) {
        this.pattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
    }

    public void setComparator(Comparator<String> comparator) {
        this.comparator = comparator;
    }

    public TelegramMethod handle(Update update) {
        String text = update.getMessage().getText();
        if (text == null) return null;
        String choice = processText(text);
        return choice == null ? null : MessageHelper.answer(update.getMessage(), choice);
    }

    public String processText(String text) {
        Matcher matcher = pattern.matcher(text);
        if (!matcher.matches() || matcher.groupCount() < 2) return null;

        List<String> options = new ArrayList<>();
        options.addAll(Arrays.asList(matcher.group(1).split(" *, *")));
        options.add(matcher.group(2));

        return Collections.max(options, comparator);
    }
}

