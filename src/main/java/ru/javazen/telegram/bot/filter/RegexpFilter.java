package ru.javazen.telegram.bot.filter;

import org.telegram.telegrambots.meta.api.objects.Message;
import ru.javazen.telegram.bot.util.MessageHelper;

import java.util.regex.Pattern;

public class RegexpFilter implements MessageFilter {
    private static final Pattern DEFAULT_PATTERN = Pattern.compile(".*");
    private Pattern pattern = DEFAULT_PATTERN;

    public void setPattern(String pattern) {
        this.pattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
    }

    @Override
    public boolean check(Message message) {
        String text = MessageHelper.getActualText(message);
        return text != null && pattern.matcher(text).matches();
    }
}
