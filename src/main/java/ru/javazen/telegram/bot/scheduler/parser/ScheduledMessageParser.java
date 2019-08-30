package ru.javazen.telegram.bot.scheduler.parser;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.telegram.telegrambots.api.objects.Message;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class ScheduledMessageParser {

    protected final String REPEAT_PATTERN = "(.*)? и повторяй каждые( .*)";
    private final Pattern repeatPattern;

    protected final TimeParser timeParser;

    public ScheduledMessageParser(TimeParser parser) {
        this.timeParser = parser;
        repeatPattern = Pattern.compile(REPEAT_PATTERN, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);

    }

    public long parseRepeatPeriod(String message) {
        Matcher matcher = repeatPattern.matcher(message);
        if (matcher.matches()) {
            return timeParser.parsePeriod(matcher.group(matcher.groupCount()));
        }
        return 0;
    }

    public boolean isRepeatable(String message) {
        Matcher matcher = repeatPattern.matcher(message);
        return matcher.matches();
    }

    public String getMessage(String text) {
        Matcher matcher = repeatPattern.matcher(text);
        if (matcher.matches()) {
            return matcher.group(1);
        }
        return "";
    }

    public abstract ParseResult parse(String text, Message message);

    public abstract boolean canParse(String text);

    @Getter
    @Setter
    @AllArgsConstructor
    public class ParseResult {
        private Date date;
        private String message;
        private long repeatPeriod;
    }
}
