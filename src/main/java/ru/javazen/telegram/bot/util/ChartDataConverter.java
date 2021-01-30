package ru.javazen.telegram.bot.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Component;
import ru.javazen.telegram.bot.datasource.model.ChartData;
import ru.javazen.telegram.bot.datasource.model.PeriodStatistic;
import ru.javazen.telegram.bot.model.ChatEntity;
import ru.javazen.telegram.bot.model.UserEntity;

import java.util.*;
import java.util.function.ToLongFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.summingLong;

@Component
public class ChartDataConverter {
    public ChartData convert(List<? extends PeriodStatistic<?>> source, Attribute attribute) {
        Map<Object, Long> totalCounts = source.stream()
                .filter(statistic -> statistic.getSubject() != null)
                .collect(Collectors.groupingBy(PeriodStatistic::getSubject, summingLong(attribute.function)));

        List<Object> subjects = source.stream()
                .map(PeriodStatistic::getSubject)
                .filter(Objects::nonNull)
                .distinct()
                .sorted(Comparator.comparing(totalCounts::get).reversed())
                .collect(Collectors.toList());
        ChartData target = new ChartData();
        target.setXKey(0);
        target.setYKeys(IntStream.range(1, subjects.size() + 1).toArray());
        target.setLabels(subjects.stream().map(this::formatLabel).toArray(String[]::new));
        Object[][] data = source.stream()
                .collect(Collectors.groupingBy(PeriodStatistic::getPeriod))
                .entrySet().stream()
                .map(entry -> formatDataRow(entry, subjects, attribute))
                .toArray(Object[][]::new);
        target.setData(data);
        return target;
    }

    private Object[] formatDataRow(Map.Entry<String, ? extends List<? extends PeriodStatistic<?>>> entry,
                                   List<Object> subjects, Attribute attribute) {
        List<? extends PeriodStatistic<?>> statistic = entry.getValue();
        Object[] result = new Object[subjects.size() + 1];
        Arrays.fill(result, 0);
        result[0] = entry.getKey();
        for (PeriodStatistic<?> userStatistic : statistic) {
            if (userStatistic.getSubject() != null) {
                int index = subjects.indexOf(userStatistic.getSubject());
                result[1 + index] = attribute.function.applyAsLong(userStatistic);
            }
        }
        return result;
    }

    private String formatLabel(Object subject) {
        if (subject instanceof UserEntity userEntity) {
            return userEntity.getLabel();
        }
        if (subject instanceof ChatEntity chatEntity) {
            return chatEntity.getLabel();
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
