package ru.javazen.telegram.bot.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Component;
import ru.javazen.telegram.bot.datasource.model.ChartData;
import ru.javazen.telegram.bot.datasource.model.PeriodStatistic;
import ru.javazen.telegram.bot.model.IdSupplier;
import ru.javazen.telegram.bot.model.LabelSupplier;

import java.sql.Timestamp;
import java.time.ZoneId;
import java.util.*;
import java.util.function.ToLongFunction;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.summingLong;

@Component
public class ChartDataConverter {
    public ChartData convert(List<? extends PeriodStatistic<?>> source, ZoneId zoneId) {
        return convert(source, Attribute.MESSAGES, false, zoneId);
    }

    public ChartData convert(List<? extends PeriodStatistic<?>> source, Attribute attribute, ZoneId zoneId) {
        return convert(source, attribute, true, zoneId);
    }

    public ChartData convert(List<? extends PeriodStatistic<?>> source, Attribute attribute, boolean sortByTotal, ZoneId zoneId) {
        List<Object> subjects = sortByTotal
                ? getSortedByTotalSubjects(source, attribute)
                : getSubjects(source);

        ChartData target = new ChartData();
        target.setIds(subjects.stream().mapToLong(this::extractId).toArray());
        target.setLabels(subjects.stream().map(this::formatLabel).toArray(String[]::new));
        Object[][] data = source.stream()
                .collect(Collectors.groupingBy(PeriodStatistic::getPeriod))
                .entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> formatDataRow(entry.getKey(), entry.getValue(), subjects, attribute, zoneId))
                .toArray(Object[][]::new);
        target.setData(data);
        return target;
    }

    private List<Object> getSubjects(List<? extends PeriodStatistic<?>> source) {
        return source.stream()
                .map(PeriodStatistic::getSubject)
                .filter(Objects::nonNull)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    private List<Object> getSortedByTotalSubjects(List<? extends PeriodStatistic<?>> source, Attribute attribute) {
        Map<Object, Long> totalCounts = source.stream()
                .filter(statistic -> statistic.getSubject() != null)
                .collect(Collectors.groupingBy(PeriodStatistic::getSubject, summingLong(attribute.function)));
        return source.stream()
                .map(PeriodStatistic::getSubject)
                .filter(Objects::nonNull)
                .distinct()
                .sorted(Comparator.comparing(totalCounts::get).reversed())
                .collect(Collectors.toList());
    }

    private Object[] formatDataRow(Timestamp period, List<? extends PeriodStatistic<?>> statistic,
                                   List<Object> subjects, Attribute attribute, ZoneId zoneId) {
        Object[] result = new Object[subjects.size() + 1];
        Arrays.fill(result, 0);
        result[0] = period.toLocalDateTime().atZone(zoneId).toLocalDateTime().toString();
        for (PeriodStatistic<?> item : statistic) {
            if (item.getSubject() != null) {
                int index = subjects.indexOf(item.getSubject());
                result[1 + index] = attribute.function.applyAsLong(item);
            }
        }
        return result;
    }

    private long extractId(Object subject) {
        if (subject instanceof IdSupplier) {
            return ((IdSupplier) subject).getId();
        }
        return subject.hashCode();
    }

    private String formatLabel(Object subject) {
        if (subject instanceof LabelSupplier) {
            return  ((LabelSupplier) subject).getLabel();
        }
        return subject.toString();
    }

    @AllArgsConstructor
    @Getter
    public enum Attribute {
        MESSAGES(PeriodStatistic::getCount),
        CHARACTERS(PeriodStatistic::getLength),
        SCORE(periodUserStatistic -> Math.round(periodUserStatistic.getScore()));

        private final ToLongFunction<PeriodStatistic<?>> function;
    }
}
