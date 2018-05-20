package ru.javazen.telegram.bot.scheduler.parser.service;

import org.languagetool.tokenizers.Tokenizer;
import org.languagetool.tokenizers.WordTokenizer;
import org.springframework.stereotype.Component;
import ru.javazen.telegram.bot.scheduler.parser.service.tructure.DateMessageStructure;
import ru.javazen.telegram.bot.scheduler.parser.service.unit.DayOfWeek;
import ru.javazen.telegram.bot.scheduler.parser.service.unit.DayOffsets;
import ru.javazen.telegram.bot.scheduler.parser.service.unit.TimeOfDay;
import ru.javazen.telegram.bot.scheduler.parser.service.unit.TimeUnitsMapper;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class TimeExtractionServiceImpl implements TimeExtractionService {

    private static final String DATE_REGEXP = "\\d{2}[.-]\\d{2}[.-]\\d{4}|\\d{1}[.-]\\d{2}[.-]\\d{4}";
    private static final String TIME_REGEXP = "\\d{2}:\\d{2}";
    private static final Pattern DATE_PATTERN = Pattern.compile(DATE_REGEXP);
    private static final Pattern TIME_PATTERN = Pattern.compile(TIME_REGEXP);

    private final TimeUnitsMapper timeUnitsMapper;

    private Tokenizer tokenizer = new WordTokenizer();

    public TimeExtractionServiceImpl(TimeUnitsMapper timeUnitsMapper) {
        this.timeUnitsMapper = timeUnitsMapper;
    }

    @Override
    public Date extractTime(String message) {

        String[] graphemes = extractGraphemes(message);
        DateMessageStructure structure = new DateMessageStructure();
        for (int i = 0; i < graphemes.length; i++) {
            TimeOfDay timeOfDay = timeUnitsMapper.resolveTimeOfDay(graphemes[i]);
            if (timeOfDay != null) {
                structure.setTimeOfDay(timeOfDay);
                structure.setTimeOfDayPos(i);
            }

            DayOfWeek daysOfWeek = timeUnitsMapper.resolveDaysOfWeek(graphemes[i]);
            if (daysOfWeek!= null) {
                structure.setDayOfWeek(daysOfWeek);
                structure.setDayOfWeekPos(i);
            }

            DayOffsets daysOffsets = timeUnitsMapper.resolveDaysOffsets(graphemes[i]);
            if (daysOffsets != null) {
                structure.setDayOffset(daysOffsets);
                structure.setDayOffsetPos(i);
            }
        }

        structure.setExplicitDate(findExplicitDate(message));
        structure.setExplicitTime(findExplicitTime(message));


        return resolve(structure);
    }

    private Date resolve(DateMessageStructure str) {

        if (str.getExplicitDate() != null && str.getExplicitTime() != null) {
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+4:00"));
            calendar.setTime(str.getExplicitDate());
            calendar.add(Calendar.HOUR_OF_DAY, str.getExplicitTime().getHour());
            calendar.add(Calendar.MINUTE, str.getExplicitTime().getMinute());

            return calendar.getTime();
        }
        //dayOffset + timeOfDay - завтра вечером
        if (
                str.getDayOffsetPos() != -1 &&
                        str.getTimeOfDayPos() != -1 &&
                        str.getDayOffsetPos() < str.getTimeOfDayPos()) {

            //Date date = new Date();
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+4:00"));

            calendar.add(Calendar.DAY_OF_YEAR, str.getDayOffset().getOffset());

            int hours = calendar.get(Calendar.HOUR_OF_DAY);
            calendar.add(Calendar.HOUR_OF_DAY, -hours);
            calendar.add(Calendar.MINUTE, -calendar.get(Calendar.MINUTE));
            calendar.add(Calendar.SECOND, -calendar.get(Calendar.SECOND));

            calendar.add(Calendar.HOUR_OF_DAY, str.getTimeOfDay().getTime());

            return calendar.getTime();

        }

        //timeOfDay + dayOfWeek - вечером в четверт
        else if (
                str.getTimeOfDayPos() != -1 &&
                str.getDayOfWeekPos() != -1) {

            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+4:00"));

            int currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

            int offset = 7 - currentDayOfWeek + str.getDayOfWeek().getOffset();
            offset = offset > 7 ? offset - 7 : offset;

            calendar.add(Calendar.DAY_OF_WEEK, offset);

            int hours = calendar.get(Calendar.HOUR_OF_DAY);
            calendar.add(Calendar.HOUR_OF_DAY, -hours);
            calendar.add(Calendar.MINUTE, -calendar.get(Calendar.MINUTE));
            calendar.add(Calendar.SECOND, -calendar.get(Calendar.SECOND));
            calendar.add(Calendar.HOUR_OF_DAY, str.getTimeOfDay().getTime());

            return calendar.getTime();

        }

        //dayOfWeek - в четверг
        else if (
                str.getDayOfWeekPos() != -1) {

            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+4:00"));

            int currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

            int offset = 7 - currentDayOfWeek + str.getDayOfWeek().getOffset();
            offset = offset > 7 ? offset - 7 : offset;
            calendar.add(Calendar.DAY_OF_WEEK, offset);

            int hours = calendar.get(Calendar.HOUR_OF_DAY);
            calendar.add(Calendar.HOUR_OF_DAY, -hours);
            calendar.add(Calendar.MINUTE, -calendar.get(Calendar.MINUTE));
            calendar.add(Calendar.SECOND, -calendar.get(Calendar.SECOND));

            if (str.getExplicitTime() == null) {
                calendar.add(Calendar.HOUR_OF_DAY, TimeOfDay.MORNING.getTime());
            } else {
                calendar.add(Calendar.HOUR_OF_DAY, str.getExplicitTime().getHour());
                calendar.add(Calendar.MINUTE, str.getExplicitTime().getMinute());
            }

            return calendar.getTime();
        }

        //timeOfDay - вечером
        else if (
                str.getTimeOfDayPos() != -1) {

            //Date date = new Date();
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+4:00"));

            if (str.getExplicitDate() != null) {
                calendar.setTime(str.getExplicitDate());
            }
            int hours = calendar.get(Calendar.HOUR_OF_DAY);
            calendar.add(Calendar.HOUR_OF_DAY, -hours);
            calendar.add(Calendar.MINUTE, -calendar.get(Calendar.MINUTE));
            calendar.add(Calendar.SECOND, -calendar.get(Calendar.SECOND));

            if (hours > str.getTimeOfDay().getTime()) {
                calendar.add(Calendar.DAY_OF_YEAR, 1);
            }

            calendar.add(Calendar.HOUR_OF_DAY, str.getTimeOfDay().getTime());
            //calendar.add(Calendar.);
            return calendar.getTime();
        }

        //dayOffset - завтра
        else if (str.getDayOffsetPos() != -1) {
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+4:00"));

            int hours = calendar.get(Calendar.HOUR_OF_DAY);
            calendar.add(Calendar.HOUR_OF_DAY, -hours);
            calendar.add(Calendar.MINUTE, -calendar.get(Calendar.MINUTE));
            calendar.add(Calendar.SECOND, -calendar.get(Calendar.SECOND));
            calendar.add(Calendar.DAY_OF_YEAR, str.getDayOffset().getOffset());
            if (str.getExplicitTime() == null) {
                calendar.add(Calendar.HOUR_OF_DAY, TimeOfDay.MORNING.getTime());
            } else {
                calendar.add(Calendar.HOUR_OF_DAY, str.getExplicitTime().getHour());
                calendar.add(Calendar.MINUTE, str.getExplicitTime().getMinute());
            }

            return calendar.getTime();
        }
        else if (str.getExplicitDate() != null) {
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+4:00"));
            calendar.setTime(str.getExplicitDate());

            calendar.add(Calendar.HOUR_OF_DAY, -calendar.get(Calendar.HOUR_OF_DAY));
            calendar.add(Calendar.MINUTE, -calendar.get(Calendar.MINUTE));
            calendar.add(Calendar.SECOND, -calendar.get(Calendar.SECOND));
            calendar.add(Calendar.HOUR_OF_DAY, TimeOfDay.MORNING.getTime());

            return calendar.getTime();
        }

        else if (str.getExplicitTime() != null) {
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+4:00"));
            Date current = calendar.getTime();

            calendar.add(Calendar.HOUR_OF_DAY, -calendar.get(Calendar.HOUR_OF_DAY));
            calendar.add(Calendar.MINUTE, -calendar.get(Calendar.MINUTE));
            calendar.add(Calendar.SECOND, -calendar.get(Calendar.SECOND));
            calendar.add(Calendar.HOUR_OF_DAY, str.getExplicitTime().getHour());
            calendar.add(Calendar.MINUTE, str.getExplicitTime().getMinute());

            if (current.getTime() > calendar.getTime().getTime()) {
                calendar.add(Calendar.DAY_OF_YEAR, 1);
            }

            return calendar.getTime();
        }

        return null;
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


    public String[] extractGraphemes(String text) {
        List<String> tokens = tokenizer.tokenize(text);
        return tokens.toArray(new String[tokens.size()]);
    }

}
