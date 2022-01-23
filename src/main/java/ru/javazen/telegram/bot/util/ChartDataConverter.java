package ru.javazen.telegram.bot.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Component;
import ru.javazen.telegram.bot.datasource.model.*;
import ru.javazen.telegram.bot.model.IdSupplier;
import ru.javazen.telegram.bot.model.LabelSupplier;

import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.function.ToLongFunction;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.summingLong;
import static java.util.stream.Collectors.toList;

@Component
public class ChartDataConverter {
    public ChartData convert(List<? extends TimestampMessageStatistic<?>> source, Attribute attribute, ZoneId zoneId) {
        var formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME.withZone(zoneId);

        var subjects = getSortedByTotalSubjects(source, attribute);

        return ChartData.builder()
                .ids(subjects.stream().map(this::extractId).collect(toList()))
                .labels(subjects.stream().map(this::formatLabel).collect(toList()))
                .data(source.stream()
                        .collect(Collectors.groupingBy(TimestampMessageStatistic::getTimestamp))
                        .entrySet().stream()
                        .sorted(Map.Entry.comparingByKey())
                        .map(entry -> formatDataRow(entry.getKey(), entry.getValue(), subjects, attribute, formatter))
                        .toArray(Object[][]::new))
                .build();
    }

    public ChartData convert(List<? extends PeriodIdMessageStatistic<?>> source, Attribute attribute, Function<Integer, String> periodNameFunc) {
        var subjects = getSortedByTotalSubjects(source, attribute);

        return ChartData.builder()
                .ids(subjects.stream().map(this::extractId).collect(toList()))
                .labels(subjects.stream().map(this::formatLabel).collect(toList()))
                .data(source.stream()
                        .collect(Collectors.groupingBy(PeriodIdMessageStatistic::getPeriodId))
                        .entrySet().stream()
                        .sorted(Map.Entry.comparingByKey())
                        .map(entry -> formatDataRow(periodNameFunc.apply(entry.getKey()), entry.getValue(), subjects, attribute))
                        .toArray(Object[][]::new))
                .build();
    }

    public ChartData convert(List<? extends TimestampEntityTypesCount> source, ZoneId zoneId) {
        var formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME.withZone(zoneId);
        return ChartData.builder()
                .id(1L)
                .label("Chats")
                .id(2L)
                .label("Users")
                .data(source.stream()
                        .map(item -> new Object[]{
                                formatter.format(item.getTimestamp().toInstant()),
                                item.getChatCount(),
                                item.getUserCount()
                        })
                        .toArray(Object[][]::new))
                .build();
    }

    private List<Object> getSortedByTotalSubjects(List<? extends MessageStatistic<?>> source, Attribute attribute) {
        var totalCounts = source.stream()
                .filter(statistic -> statistic.getSubject() != null)
                .collect(Collectors.groupingBy(MessageStatistic::getSubject, summingLong(attribute.function)));
        return source.stream()
                .map(MessageStatistic::getSubject)
                .filter(Objects::nonNull)
                .distinct()
                .sorted(Comparator.comparing(totalCounts::get).reversed())
                .collect(toList());
    }

    private Object[] formatDataRow(Timestamp period, List<? extends MessageStatistic<?>> statistic,
                                   List<Object> subjects, Attribute attribute, DateTimeFormatter formatter) {
        var result = new Object[subjects.size() + 1];
        Arrays.fill(result, 0);
        result[0] = formatter.format(period.toInstant());
        for (MessageStatistic<?> item : statistic) {
            if (item.getSubject() != null) {
                int index = subjects.indexOf(item.getSubject());
                result[1 + index] = attribute.function.applyAsLong(item);
            }
        }
        return result;
    }

    private Object[] formatDataRow(String periodName, List<? extends MessageStatistic<?>> statistic,
                                   List<Object> subjects, Attribute attribute) {
        var result = new Object[subjects.size() + 1];
        Arrays.fill(result, 0);
        result[0] = periodName;
        for (MessageStatistic<?> item : statistic) {
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
        MESSAGES(MessageStatistic::getCount),
        CHARACTERS(MessageStatistic::getLength),
        SCORE(periodUserStatistic -> Math.round(periodUserStatistic.getScore()));

        private final ToLongFunction<MessageStatistic<?>> function;
    }
}
