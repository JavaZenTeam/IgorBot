package ru.javazen.telegram.bot.scheduler.parser;

import org.springframework.util.StringUtils;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.javazen.telegram.bot.util.MessageHelper;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.springframework.util.StringUtils.isEmpty;

public class ShiftTimeParser extends ScheduledWithRepetitionParser {

    private final static int COMMAND_GROUP = 1;

    private final Supplier<String> defaultMessageSupplier;
    private final Pattern activationPattern;

    public ShiftTimeParser(Supplier<String> defaultMessageSupplier, String activationPattern) {
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
            Matcher commandMatcher = TIME_UNITS_PATTERN.matcher(command);
            return commandMatcher.matches() && matchedCount(commandMatcher) > 1;
        }

        return false;
    }

    private ParseResult parseParameters(String text, Message message) {


        Matcher matcher = TIME_UNITS_PATTERN.matcher(text);

        GregorianCalendar calendar = new GregorianCalendar();
        boolean calendarChanged = false;


        if(matcher.matches()) {
            String time;
            int value;

            for (int i = 1; i < TIME_UNITS.length; i++) {
                if (matcher.group(i) != null && !matcher.group(i).isEmpty()) {
                    time = matcher.group(i).replaceAll("\\D", "");
                    value = !StringUtils.isEmpty(time) ? Integer.parseInt(time) : 1;
                    calendarChanged = true;
                    calendar.add(TIME_UNITS[i], value);
                }
            }
        }

        RepetitionParsedResult repetitionResult = parseRepetition(matcher.group(matcher.groupCount()));
        String returnMessage = repetitionResult.getText();
        Integer repetitions = repetitionResult.getRepetitions();
        String interval = repetitionResult.getInterval();

        if (isEmpty(returnMessage) && message.getReplyToMessage() != null) {
            returnMessage = MessageHelper.getActualText(message.getReplyToMessage());
        }
        if (isEmpty(returnMessage)) {
            returnMessage = defaultMessageSupplier.get();
        }

        if (!calendarChanged) {
            throw new IllegalArgumentException("No time span specified");
        }

        return new ParseResult(calendar.getTime(), returnMessage.trim(), repetitions, interval);
    }

    private int matchedCount(Matcher matcher) {
        int count = 0;
        for (int i = 0; i < matcher.groupCount(); i++) {
            if (matcher.group(i) != null) {
                count++;
            }
        }
        return count;
    }
}
