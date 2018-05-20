package ru.javazen.telegram.bot.scheduler.parser.service.unit;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class TimeUnitsMapper {

    private Map<List<String>, TimeOfDay> timesOfDayMap = new HashMap<>();

    private Map<List<String>, DayOfWeek> daysOfWeekMap = new HashMap<>();

    private Map<List<String>, DayOffsets> daysOffsetsMap = new HashMap<>();

    @PostConstruct
    public void init() {
        initDayOffsets();
        initDaysOfWeek();
        initTimesOfDay();
    }

    private void initTimesOfDay() {
        List<String> morning = Arrays.asList("утро", "утром", "с утра");
        timesOfDayMap.put(morning, TimeOfDay.MORNING);

        List<String> day = Arrays.asList("день", "днем");
        timesOfDayMap.put(day, TimeOfDay.DAY);

        List<String> evening = Arrays.asList("вечер", "вечером", "с вечера");
        timesOfDayMap.put(evening, TimeOfDay.EVENING);

        List<String> night = Arrays.asList("ночь", "ночью");
        timesOfDayMap.put(night, TimeOfDay.NIGHT);
    }

    private void initDaysOfWeek() {
        List<String> monday = Arrays.asList("понедельник");
        daysOfWeekMap.put(monday, DayOfWeek.MONDAY);

        List<String> tuesday = Arrays.asList("вторник");
        daysOfWeekMap.put(tuesday, DayOfWeek.TUESDAY);

        List<String> wednesday = Arrays.asList("среда", "среду");
        daysOfWeekMap.put(wednesday, DayOfWeek.WEDNESDAY);

        List<String> thursday = Arrays.asList("четверг");
        daysOfWeekMap.put(thursday, DayOfWeek.THURSDAY);

        List<String> friday = Arrays.asList("пятница", "пятницу");
        daysOfWeekMap.put(friday, DayOfWeek.FRIDAY);

        List<String> saturday = Arrays.asList("суббота", "субботу");
        daysOfWeekMap.put(saturday, DayOfWeek.SATURDAY);

        List<String> sunday = Arrays.asList("воскресенье");
        daysOfWeekMap.put(sunday, DayOfWeek.SUNDAY);
    }

    private void initDayOffsets() {
        List<String> today = Arrays.asList("сегодня");
        daysOffsetsMap.put(today, DayOffsets.TODAY);

        List<String> tomorrow = Arrays.asList("завтра");
        daysOffsetsMap.put(tomorrow, DayOffsets.TOMORROW);

        List<String> dayAfterTomorrow = Arrays.asList("послезавтра");
        daysOffsetsMap.put(dayAfterTomorrow, DayOffsets.DAY_AFTER_TOMORROW);
    }

    public TimeOfDay resolveTimeOfDay(String word) {

        Optional<TimeOfDay> timesOfDay = timesOfDayMap.entrySet().stream()
                .filter(e -> e.getKey().contains(word) || e.getKey().contains(word))
                .map(Map.Entry::getValue).findAny();

        return timesOfDay.orElse(null);
    }

    public DayOffsets resolveDaysOffsets(String word) {

        Optional<DayOffsets> daysOffsets = daysOffsetsMap.entrySet().stream()
                .filter(e -> e.getKey().contains(word) || e.getKey().contains(word))
                .map(Map.Entry::getValue).findAny();

        return daysOffsets.orElse(null);
    }

    public DayOfWeek resolveDaysOfWeek(String word) {
        System.out.println(word);
        Optional<DayOfWeek> timesOfDay = daysOfWeekMap.entrySet().stream()
                .filter(e -> e.getKey().contains(word) || e.getKey().contains(word))
                .map(Map.Entry::getValue).findAny();

        return timesOfDay.orElse(null);
    }
}
