package ru.javazen.telegram.bot.scheduler.parser;

import org.telegram.telegrambots.api.objects.Update;
import ru.javazen.telegram.bot.scheduler.parser.service.TimeExtractionService;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpecificTimeParser implements ScheduledMessageParser {

    private static final String DATE_REGEXP = "";
    private static final String TIME_REGEXP = "";
    private static final Pattern DATE_PATTERN = Pattern.compile(DATE_REGEXP);
    private static final Pattern TIME_PATTERN = Pattern.compile(TIME_REGEXP);

    private final static String PATTERN = /*"(\\d{2}[.-]\\d{2}[.-]\\d{4}|\\d{1}[.-]\\d{2}[.-]\\d{4})?" +*/
            "( ?в \\d{2}:\\d{2})?" +
            "( ?сегодня|завтра)?" +
            "( ?утром|днем|вечером|ночью)?" +
            "( ?в понедельник|вторник|среду|четверг|пятницу|субботу|воскресение)" +
            "(.*)?$";

    private final Supplier<String> defaultMessageSupplier;
    private final String validationPattern;
    private final TimeExtractionService timeExtractionService;

    public SpecificTimeParser(Supplier<String> defaultMessageSupplier,
                              String validationPattern,
                              TimeExtractionService timeExtractionService) {
        this.defaultMessageSupplier = defaultMessageSupplier;
        this.validationPattern = validationPattern;
        this.timeExtractionService = timeExtractionService;
    }

    @Override
    public ParseResult parse(String message, Update update) {

        Pattern pattern0 = Pattern.compile(validationPattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.DOTALL);
        Matcher matcher0 = pattern0.matcher(message);

        if (matcher0.matches()) {
            String command = matcher0.group(1);

            Pattern pattern = Pattern.compile(PATTERN, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
            Matcher matcher = pattern.matcher(command);

            System.out.println(command);

            if (matcher.matches()) {
                String text = matcher.group(matcher.groupCount());
                Date date = timeExtractionService.extractTime(message);

                ParseResult result = new ParseResult();
                result.setDate(date);
                result.setMessage(text);
                return result;
            }
        }
        return null;
    }

    @Override
    public boolean canParse(String message) {
        Pattern pattern = Pattern.compile(validationPattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.DOTALL);
        Matcher matcher = pattern.matcher(message);

        return matcher.matches();
    }

    private Date findExplicitDate(String s) {
        Matcher matcher = DATE_PATTERN.matcher(s);
        if (matcher.find()) {
            String dateStr = matcher.group(0);
            dateStr = dateStr.replace(".", "-");
            DateFormat dateFormat = new SimpleDateFormat("dd-MM-yy");
            try {
                return dateFormat.parse(dateStr);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private LocalTime findExplicitTime(String s) {
        Matcher matcher = TIME_PATTERN.matcher(s);
        if (matcher.find()) {
            String timeStr = matcher.group(0);
            return LocalTime.parse(timeStr, DateTimeFormatter.ofPattern("HH:mm"));
        }
        return null;
    }
}
