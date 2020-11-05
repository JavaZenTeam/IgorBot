package ru.javazen.telegram.bot.preprocessor;

import org.telegram.telegrambots.meta.api.objects.Message;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.function.BiFunction;
import java.util.regex.Pattern;

public class ReplaceDateTimeAdverb implements BiFunction<Message, String, String> {
    private SimpleDateFormat formatter;
    private Pattern adverbPattern;
    private int offsetField;
    private int offsetAmount;

    public ReplaceDateTimeAdverb(String adverb, String formatPattern, int offsetField, int offsetAmount) {
        this.formatter = new SimpleDateFormat(formatPattern);
        this.adverbPattern = Pattern.compile("\\b" + adverb + "\\b", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
        this.offsetField = offsetField;
        this.offsetAmount = offsetAmount;
    }

    public ReplaceDateTimeAdverb(String adverb, String formatPattern) {
        this(adverb, formatPattern, 0, 0);
    }

    @Override
    public String apply(Message message, String text) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(message.getDate() * 1000);
        calendar.add(offsetField, offsetAmount);
        String dateTime = formatter.format(calendar.getTime());

        return adverbPattern.matcher(text).replaceAll(dateTime);
    }
}
