package ru.javazen.telegram.bot.scheduler.parser;

import org.telegram.telegrambots.api.objects.Update;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpecificTimeParser implements ScheduledMessageParser {

    private final static String PATTERN = " ?(\\d{2}[.-]\\d{2}[.-]\\d{4}|\\d{1}[.-]\\d{2}[.-]\\d{4})?" +
            "( ?в (\\d{2}:\\d{2}))?" +
            "(.*)?$";

    private final Supplier<String> defaultMessageSupplier;
    private final String validationPattern;

    public SpecificTimeParser(Supplier<String> defaultMessageSupplier,
                              String validationPattern) {
        this.defaultMessageSupplier = defaultMessageSupplier;
        this.validationPattern = validationPattern;
    }

    @Override
    public ParseResult parse(String message, Update update) {

        Pattern activationPattern = Pattern.compile(validationPattern,
                Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.DOTALL);

        Matcher activation = activationPattern.matcher(message);

        if (activation.matches()) {
            String command = activation.group(1);

            Pattern pattern = Pattern.compile(PATTERN, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
            Matcher matcher = pattern.matcher(command);

            if (matcher.matches()) {
                String text = matcher.group(matcher.groupCount());

                Date date = null;

                String dateStr = matcher.group(1);
                if (dateStr != null) {
                    date = findExplicitDate(dateStr);
                }
                LocalTime time = null;

                String timeStr = matcher.group(3);
                if (timeStr != null) {
                    time = findExplicitTime(timeStr);
                }

                return new ParseResult(resolveDateTime(date, time), text);
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


    private Date resolveDateTime(Date date, LocalTime time) {

        if (date != null && time != null) {
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+4:00"));
            calendar.setTime(date);
            calendar.add(Calendar.HOUR_OF_DAY, time.getHour());
            calendar.add(Calendar.MINUTE, time.getMinute());

            return calendar.getTime();
        } else if (date != null) {
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+4:00"));

            int second = calendar.get(Calendar.SECOND);
            int minute = calendar.get(Calendar.MINUTE);
            int hour = calendar.get(Calendar.HOUR_OF_DAY);

            calendar.setTime(date);

            calendar.add(Calendar.SECOND, second);
            calendar.add(Calendar.MINUTE, minute);
            calendar.add(Calendar.HOUR_OF_DAY, hour);

            return calendar.getTime();
        } else if (time != null) {
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+4:00"));
            Date current = calendar.getTime();

            calendar.add(Calendar.HOUR_OF_DAY, -calendar.get(Calendar.HOUR_OF_DAY));
            calendar.add(Calendar.MINUTE, -calendar.get(Calendar.MINUTE));
            calendar.add(Calendar.SECOND, -calendar.get(Calendar.SECOND));
            calendar.add(Calendar.HOUR_OF_DAY, time.getHour());
            calendar.add(Calendar.MINUTE, time.getMinute());

            if (current.getTime() > calendar.getTime().getTime()) {
                calendar.add(Calendar.DAY_OF_YEAR, 1);
            }

            return calendar.getTime();
        }

        return null;
    }

    private Date findExplicitDate(String dateStr) {
        dateStr = dateStr.replace(".", "-");
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yy");
        try {
            return dateFormat.parse(dateStr);
        } catch (ParseException e) {
            throw new RuntimeException("Can't parse date: " + dateStr, e);
        }
    }

    private LocalTime findExplicitTime(String timeStr) {
        return LocalTime.parse(timeStr, DateTimeFormatter.ofPattern("HH:mm"));
    }
}
