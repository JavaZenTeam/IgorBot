package ru.javazen.telegram.bot.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import ru.javazen.telegram.bot.entity.request.Update;
import ru.javazen.telegram.bot.service.MessageHelper;
import ru.javazen.telegram.bot.service.TelegramService;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChoiceMaker implements UpdateHandler{
    private static final String OPTIONS_GROUP_NAME = "options";

    private Pattern pattern;
    private String splitPattern;
    private Comparator<String> comparator;

    @Autowired
    private TelegramService telegramService;

    @Required
    public void setPattern(String pattern) {
        this.pattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.DOTALL);
    }

    @Required
    public void setSplitPattern(String splitPattern) {
        this.splitPattern = splitPattern;
    }

    @Required
    public void setComparator(Comparator<String> comparator) {
        this.comparator = comparator;
    }

    public Comparator<String> getComparator() {
        return comparator;
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
        if (!matcher.matches()) return null;

        String optionsGroup = matcher.group(OPTIONS_GROUP_NAME);
        if (optionsGroup == null) return null;

        List<String> options = Arrays.asList(optionsGroup.split(splitPattern));
        return Collections.max(options, getComparator());
    }
}

