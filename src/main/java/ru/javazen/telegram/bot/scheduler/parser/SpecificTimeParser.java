package ru.javazen.telegram.bot.scheduler.parser;

import org.telegram.telegrambots.api.objects.Message;
import ru.javazen.telegram.bot.service.ChatConfigService;
import ru.javazen.telegram.bot.util.MessageHelper;

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

import static org.springframework.util.StringUtils.isEmpty;

public class SpecificTimeParser implements ScheduledMessageParser {

    private final static String PATTERN = " ?(\\d{2}[.-]\\d{2}[.-]\\d{4}|\\d{1}[.-]\\d{2}[.-]\\d{4})?" +
            "( ?в (\\d{2}:\\d{2}))?" +
            "(.*)?$";

    private static final String TIMEZONE_OFFSET_CONFIG_KEY = "TIMEZONE_OFFSET";

    private final Supplier<String> defaultMessageSupplier;
    private final String validationPattern;
    private final ChatConfigService chatConfigService;

    public SpecificTimeParser(Supplier<String> defaultMessageSupplier,
                              String validationPattern,
                              ChatConfigService chatConfigService) {
        this.defaultMessageSupplier = defaultMessageSupplier;
        this.validationPattern = validationPattern;
        this.chatConfigService = chatConfigService;
    }

    @Override
    public ParseResult parse(String text, Message message) {

        TimeZone timeZone = TimeZone.getTimeZone("GMT" + chatConfigService.getProperty(
                message.getFrom().getId(),
                TIMEZONE_OFFSET_CONFIG_KEY).orElse("+04:00"));

        Pattern activationPattern = Pattern.compile(validationPattern,
                Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.DOTALL);

        Matcher activation = activationPattern.matcher(text);

        if (activation.matches()) {
            String command = activation.group(1);

            Pattern pattern = Pattern.compile(PATTERN, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
            Matcher matcher = pattern.matcher(command);

            if (matcher.matches()) {
                String returnMessage = matcher.group(matcher.groupCount());
                if (isEmpty(returnMessage) && message.getReplyToMessage() != null) {
                    returnMessage = MessageHelper.getActualText(message.getReplyToMessage());
                }
                if (isEmpty(returnMessage)) {
                    returnMessage = defaultMessageSupplier.get();
                }

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

                Date parsedData = resolveDateTime(date, time, timeZone);
                if (parsedData != null) {
                    return new ParseResult(parsedData, returnMessage.trim());
                }
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


    private Date resolveDateTime(Date date, LocalTime time, TimeZone timeZone) {

        Calendar calendar = Calendar.getInstance(timeZone);

        if (date != null && time != null) {

            calendar.setTime(date);
            calendar.add(Calendar.HOUR_OF_DAY, time.getHour());
            calendar.add(Calendar.MINUTE, time.getMinute());

            return calendar.getTime();
        } else if (date != null) {

            int second = calendar.get(Calendar.SECOND);
            int minute = calendar.get(Calendar.MINUTE);
            int hour = calendar.get(Calendar.HOUR_OF_DAY);

            calendar.setTime(date);

            calendar.add(Calendar.SECOND, second);
            calendar.add(Calendar.MINUTE, minute);
            calendar.add(Calendar.HOUR_OF_DAY, hour);

            return calendar.getTime();
        } else if (time != null) {
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
