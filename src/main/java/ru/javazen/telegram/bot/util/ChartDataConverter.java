package ru.javazen.telegram.bot.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Component;
import ru.javazen.telegram.bot.datasource.model.ChartData;
import ru.javazen.telegram.bot.datasource.model.PeriodEntityTypesCount;
import ru.javazen.telegram.bot.datasource.model.PeriodMessageStatistic;
import ru.javazen.telegram.bot.datasource.model.BaseCount;
import ru.javazen.telegram.bot.model.IdSupplier;
import ru.javazen.telegram.bot.model.LabelSupplier;

import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.ToLongFunction;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.summingLong;
import static java.util.stream.Collectors.toList;

@Component
public class ChartDataConverter {
    public ChartData convert(List<? extends PeriodMessageStatistic<?>> source, Attribute attribute, ZoneId zoneId) {
        return convert(source, attribute, true, zoneId);
    }

    public ChartData convert(List<? extends PeriodEntityTypesCount> source, ZoneId zoneId) {
        var formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME.withZone(zoneId);
        return ChartData.builder()
                .id(1L)
                .label("Chats")
                .id(2L)
                .label("Users")
                .data(source.stream()
                        .map(item -> new Object[]{
                                formatter.format(item.getPeriod().toInstant()),
                                item.getChatCount(),
                                item.getUserCount()
                        })
                        .toArray(Object[][]::new))
                .build();
    }

    public ChartData convert(List<? extends PeriodMessageStatistic<?>> source, Attribute attribute, boolean sortByTotal, ZoneId zoneId) {
        var formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME.withZone(zoneId);

        var subjects = sortByTotal
                ? getSortedByTotalSubjects(source, attribute)
                : getSubjects(source);

        return ChartData.builder()
                .ids(subjects.stream().map(this::extractId).collect(toList()))
                .labels(subjects.stream().map(this::formatLabel).collect(toList()))
                .data(source.stream()
                        .collect(Collectors.groupingBy(PeriodMessageStatistic::getPeriod))
                        .entrySet().stream()
                        .sorted(Map.Entry.comparingByKey())
                        .map(entry -> formatDataRow(entry.getKey(), entry.getValue(), subjects, attribute, formatter))
                        .toArray(Object[][]::new))
                .build();
    }

    private List<Object> getSubjects(List<? extends BaseCount<?>> source) {
        return source.stream()
                .map(BaseCount::getSubject)
                .filter(Objects::nonNull)
                .distinct()
                .sorted()
                .collect(toList());
    }

    private List<Object> getSortedByTotalSubjects(List<? extends PeriodMessageStatistic<?>> source, Attribute attribute) {
        var totalCounts = source.stream()
                .filter(statistic -> statistic.getSubject() != null)
                .collect(Collectors.groupingBy(PeriodMessageStatistic::getSubject, summingLong(attribute.function)));
        return source.stream()
                .map(PeriodMessageStatistic::getSubject)
                .filter(Objects::nonNull)
                .distinct()
                .sorted(Comparator.comparing(totalCounts::get).reversed())
                .collect(toList());
    }

    private Object[] formatDataRow(Timestamp period, List<? extends PeriodMessageStatistic<?>> statistic,
                                   List<Object> subjects, Attribute attribute, DateTimeFormatter formatter) {
        var result = new Object[subjects.size() + 1];
        Arrays.fill(result, 0);
        result[0] = formatter.format(period.toInstant());
        for (PeriodMessageStatistic<?> item : statistic) {
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
            return ((LabelSupplier) subject).getLabel();
        }
        return subject.toString();
    }

    @AllArgsConstructor
    @Getter
    public enum Attribute {
        MESSAGES(PeriodMessageStatistic::getCount),
        CHARACTERS(PeriodMessageStatistic::getLength),
        SCORE(periodUserStatistic -> Math.round(periodUserStatistic.getScore()));

        private final ToLongFunction<PeriodMessageStatistic<?>> function;
    }
}
