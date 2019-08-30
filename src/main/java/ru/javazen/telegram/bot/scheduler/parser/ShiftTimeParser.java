package ru.javazen.telegram.bot.scheduler.parser;

import org.springframework.util.StringUtils;
import org.telegram.telegrambots.api.objects.Message;
import ru.javazen.telegram.bot.util.MessageHelper;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Math.toIntExact;
import static org.springframework.util.StringUtils.isEmpty;

public class ShiftTimeParser extends ScheduledMessageParser {

    private final static int COMMAND_GROUP = 1;

    private final Supplier<String> defaultMessageSupplier;
    private final Pattern activationPattern;

    public ShiftTimeParser(Supplier<String> defaultMessageSupplier, String activationPattern, TimeParser timeParser) {
        super(timeParser);
        this.defaultMessageSupplier = defaultMessageSupplier;
        this.activationPattern = Pattern.compile(activationPattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.DOTALL);
    }

    @Override
    public ParseResult parse(String text, Message message) {
        Matcher matcher = activationPattern.matcher(text);

        if (matcher.matches()) {
            String command = matcher.group(COMMAND_GROUP);
            return parseParameters(command, message);
        }

        return null;
    }

    @Override
    public boolean canParse(String message) {

        Matcher matcher = activationPattern.matcher(message);
        if (matcher.matches()) {
            String command = matcher.group(COMMAND_GROUP);
            return timeParser.canParse(command);
        }

        return false;
    }

    private ParseResult parseParameters(String text, Message message) {
        Long period = timeParser.parsePeriod(text);
        GregorianCalendar calendar = new GregorianCalendar();
        boolean calendarChanged = period > 0;
        calendar.add(Calendar.MILLISECOND, toIntExact(period));
        Long repeatPeroid = 0l;
        String returnMessage = timeParser.getMessage(text);
        if (isRepeatable(returnMessage)) {
            repeatPeroid = parseRepeatPeriod(returnMessage);
            returnMessage = getMessage(returnMessage);
        }
        if (isEmpty(returnMessage) && message.getReplyToMessage() != null) {
            returnMessage = MessageHelper.getActualText(message.getReplyToMessage());
        }
        if (isEmpty(returnMessage)) {
            returnMessage = defaultMessageSupplier.get();
        }

        if (!calendarChanged) {
            throw new IllegalArgumentException("No time span specified");
        }

        return new ParseResult(calendar.getTime(), returnMessage.trim(), repeatPeroid);
    }
}
