package ru.javazen.telegram.bot.handler;

import org.springframework.beans.factory.annotation.Autowired;
import ru.javazen.telegram.bot.entity.request.Update;
import ru.javazen.telegram.bot.method.TelegramMethod;
import ru.javazen.telegram.bot.service.MessageHelper;
import ru.javazen.telegram.bot.service.TelegramService;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChoiceMaker implements UpdateHandler{
    private static final Pattern DEFAULT_PATTERN = Pattern.compile("(.+) or (.+)\\?");
    private Pattern pattern = DEFAULT_PATTERN;
    private Comparator<String> comparator;

    @Autowired
    private TelegramService telegramService;

    public void setPattern(String pattern) {
        this.pattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
    }

    public void setComparator(Comparator<String> comparator) {
        this.comparator = comparator;
    }

    @Override
    public boolean handle(Update update, String token) {
        String text = update.getMessage().getText();
        if (text == null) return false;
        String choice = processText(text);
        if(choice != null) {
            telegramService.execute(MessageHelper.answer(update.getMessage(), choice), token);
            return true;
        }
        return false;
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

