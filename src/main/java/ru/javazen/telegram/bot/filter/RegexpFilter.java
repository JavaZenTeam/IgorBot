package ru.javazen.telegram.bot.filter;

import ru.javazen.telegram.bot.entity.Update;
import ru.javazen.telegram.bot.util.MessageHelper;

import java.util.regex.Pattern;

public class RegexpFilter implements Filter {
    private static final Pattern DEFAULT_PATTERN = Pattern.compile(".*");
    private Pattern pattern = DEFAULT_PATTERN;

    public void setPattern(String pattern) {
        this.pattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
    }

    @Override
    public boolean check(Update update) {
        String text = MessageHelper.getActualText(update.getMessage());
        return text != null && pattern.matcher(text).matches();
    }
}
