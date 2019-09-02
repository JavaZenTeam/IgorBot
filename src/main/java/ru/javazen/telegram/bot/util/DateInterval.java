package ru.javazen.telegram.bot.util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateInterval {
    
    private final static String INTERVAL_REGEXP = "(\\d*)y(\\d*)mo(\\d*)w(\\d*)d(\\d*)h(\\d*)m(\\d*)s";

    private final static int[] TIME_UNITS = {
        Calendar.YEAR,
        Calendar.MONTH,
        Calendar.WEEK_OF_YEAR,
        Calendar.DAY_OF_YEAR,
        Calendar.HOUR,
        Calendar.MINUTE,
        Calendar.SECOND
    };

    private final static String[][] TEXT_TIME_UNITS_RUS = {
            { "год", "года", "лет" },
            { "месяц", "месяца", "месяцев" },
            { "неделю", "недели", "недель" },
            { "день", "дня", "дней" },
            { "час", "часа", "часов" },
            { "минуту", "минуты", "минут" },
            { "секунду", "секунды", "секунд" }
    };

    public static Date apply(String interval, Date date) {

        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        Pattern pattern = Pattern.compile(INTERVAL_REGEXP);
        Matcher matcher = pattern.matcher(interval);

        matcher.matches();
        for (int i = 0; i < TIME_UNITS.length; i++) {
            String match = matcher.group(i + 1);
            if (!match.isEmpty()) {
                calendar.add(TIME_UNITS[i], Integer.parseInt(match));
            }
        }

        return calendar.getTime();
    }


    public static String getText(String interval) {

        Pattern pattern = Pattern.compile(INTERVAL_REGEXP);
        Matcher matcher = pattern.matcher(interval);
        StringBuilder sb = new StringBuilder();

        matcher.matches();
        for (int i = 0; i < TIME_UNITS.length; i++) {
            String match = matcher.group(i + 1);
            if (!match.isEmpty()) {
                int value = Integer.parseInt(match);
                sb.append(match + " ");
                sb.append(TEXT_TIME_UNITS_RUS[i][value == 1 ? 0 : value > 1 && value < 5 ? 1 : 2] + " ");
            }
        }

        return sb.toString();
    }
}
