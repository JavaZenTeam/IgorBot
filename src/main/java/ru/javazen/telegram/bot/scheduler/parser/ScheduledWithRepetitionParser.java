package ru.javazen.telegram.bot.scheduler.parser;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.util.StringUtils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

public abstract class ScheduledWithRepetitionParser implements ScheduledMessageParser {

    protected final static String TIME_UNITS_REGEXP = /* 1 */"( \\d{0,9} ?(?:л|лет|г|год|года))?" +
    /* 2 */"( \\d{0,9} ?(?:мес|месяц|месяца|месяцев))?" +
    /* 3 */"( \\d{0,9} ?(?:н|нед|недель|неделю|недели))?" +
    /* 4 */"( \\d{0,9} ?(?:д|дн|дней|дня|день))?" +
    /* 5 */"( \\d{0,9} ?(?:ч|час|часа|часов))?" +
    /* 6 */"( \\d{0,9} ?(?:м|мин|минуту|минуты|минут))?" +
    /* 7 */"( \\d{0,9} ?(?:с|сек|секунду|секунды|секунд))?" +
    /* 8 */"( .*)?$";
    
    protected final static int[] TIME_UNITS = {
        0,
        /* 1 */Calendar.YEAR,
        /* 2 */Calendar.MONTH,
        /* 3 */Calendar.WEEK_OF_YEAR,
        /* 4 */Calendar.DAY_OF_YEAR,
        /* 5 */Calendar.HOUR,
        /* 6 */Calendar.MINUTE,
        /* 7 */Calendar.SECOND
    };

    protected final static Pattern TIME_UNITS_PATTERN = Pattern.compile(TIME_UNITS_REGEXP, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
    
    private final static String REPETITION_REGEXP = "(.*?)(?: и|,) п[оа]вт[оа]р(?:и|яй) кажд(?:ы[йе]|ую)" + TIME_UNITS_REGEXP;

    private final static String TIMES_REGEXP = " (\\d{1,9}) ?раза?$";

    private final static String[] REPETITION_INTERVAL_TIME_UNITS = { "y", "mo", "w", "d", "h", "m", "s" };

    private final static Pattern REPETITION_PATTERN = Pattern.compile(REPETITION_REGEXP, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);

    private final static Pattern REPETITION_TIMES_PATTERN = Pattern.compile(TIMES_REGEXP, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);

    public RepetitionParsedResult parseRepetition(String text) {

        Integer repetitions = -1; // forever
        String returnMessage = text;

        Matcher matcher = REPETITION_PATTERN.matcher(text);
        StringBuilder sb = new StringBuilder();
        if (matcher.matches()) {
            returnMessage = matcher.group(1);
            for (int i = 1; i < TIME_UNITS.length; i++) {
                String group = matcher.group(i + 1);
                if (group != null && !group.isEmpty()) {
                    String value = group.replaceAll("\\D", "");
                    sb.append(!StringUtils.isEmpty(value) ? Integer.parseInt(value) : "1");
                }
                sb.append(REPETITION_INTERVAL_TIME_UNITS[i - 1]);
            }

            String times = matcher.group(matcher.groupCount());
            if (times != null && !times.isEmpty()) {
                Matcher timesMatcher = REPETITION_TIMES_PATTERN.matcher(times);
                if (timesMatcher.matches()) {
                    repetitions = Integer.parseInt(timesMatcher.group(1));
                }
            }
        } else {
            return new RepetitionParsedResult(null, null, returnMessage);
        }

        return new RepetitionParsedResult(repetitions, sb.toString(), returnMessage);
    }

    @Getter
    @Setter
    @AllArgsConstructor
    class RepetitionParsedResult {
        private Integer repetitions;
        private String interval;
        private String text;
    }
}