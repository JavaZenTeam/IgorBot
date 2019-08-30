package ru.javazen.telegram.bot.scheduler.parser;

import org.springframework.util.StringUtils;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Math.toIntExact;

public class TimeParser {
    private final static String TIME_UNITS_REGEXP = /* 1 */"( \\d* ?(?:л|лет|г|год|года))?" +
            /* 2 */"( \\d* ?(?:мес|месяц|месяца|месяцев))?" +
            /* 3 */"( \\d* ?(?:н|нед|недель|неделю|недели))?" +
            /* 4 */"( \\d* ?(?:д|дн|дней|дня|день))?" +
            /* 5 */"( \\d* ?(?:ч|час|часа|часов))?" +
            /* 6 */"( \\d* ?(?:м|мин|минуту|минуты|минут))?" +
            /* 7 */"( \\d* ?(?:с|сек|секунду|секунды|секунд))?" +
            /* 8 */"( .*)?$";

    private final static ChronoUnit[] CHRONO_UNITS = {
            ChronoUnit.YEARS,
            ChronoUnit.MONTHS,
            ChronoUnit.WEEKS,
            ChronoUnit.DAYS,
            ChronoUnit.HOURS,
            ChronoUnit.MINUTES,
            ChronoUnit.SECONDS
    };
    private final static Pattern TIME_UNITS_PATTERN = Pattern.compile(TIME_UNITS_REGEXP, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);

    public Long parsePeriod(String text) {
        Matcher matcher = TIME_UNITS_PATTERN.matcher(text);
        Long totalPeriod = 0l;
        if (matcher.matches()) {
            String time;
            int value;

            for (int i = 1; i <= CHRONO_UNITS.length; i++) {
                if (matcher.group(i) != null && !matcher.group(i).isEmpty()) {
                    time = matcher.group(i).replaceAll("\\D", "");
                    value = !StringUtils.isEmpty(time) ? Integer.parseInt(time) : 1;
                    Duration duration = Duration.of(value, CHRONO_UNITS[i - 1]);
                    totalPeriod += duration.toMillis();
                }
            }
        }
        return totalPeriod;
    }

    public boolean canParse(String text) {
        Matcher commandMatcher = TIME_UNITS_PATTERN.matcher(text);
        return commandMatcher.matches() && matchedCount(commandMatcher) > 1;
    }

    public String getMessage(String text) {
        Matcher matcher = TIME_UNITS_PATTERN.matcher(text);
        if (matcher.matches()) {
            return matcher.group(matcher.groupCount());
        }
        return "";
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
