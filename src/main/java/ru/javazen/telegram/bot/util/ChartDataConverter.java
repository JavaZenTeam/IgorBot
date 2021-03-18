package ru.javazen.telegram.bot.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Component;
import ru.javazen.telegram.bot.datasource.model.ChartData;
import ru.javazen.telegram.bot.datasource.model.PeriodStatistic;
import ru.javazen.telegram.bot.model.IdSupplier;
import ru.javazen.telegram.bot.model.LabelSupplier;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.ToLongFunction;
import java.util.stream.Collectors;

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
        target.setIds(subjects.stream().map(this::extractId).toArray((String[]::new)));
        target.setLabels(subjects.stream().map(this::formatLabel).toArray(String[]::new));
        Object[][] data = source.stream()
                .collect(Collectors.groupingBy(PeriodStatistic::getPeriod))
                .entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> formatDataRow(entry.getKey(), entry.getValue(), subjects, attribute))
                .toArray(Object[][]::new);
        target.setData(data);
        return target;
    }

    private Object[] formatDataRow(LocalDateTime period, List<? extends PeriodStatistic<?>> statistic,
                                   List<Object> subjects, Attribute attribute) {
        Object[] result = new Object[subjects.size() + 1];
        Arrays.fill(result, 0);
        result[0] = period.toString();
        for (PeriodStatistic<?> userStatistic : statistic) {
            if (userStatistic.getSubject() != null) {
                int index = subjects.indexOf(userStatistic.getSubject());
                result[1 + index] = attribute.function.applyAsLong(userStatistic);
            }
        }
        return result;
    }

    private String extractId(Object subject) {
        if (subject instanceof IdSupplier idSupplier) {
            return idSupplier.getId();
        }
        return subject.toString();
    }

    private String formatLabel(Object subject) {
        if (subject instanceof LabelSupplier labelSupplier) {
            return labelSupplier.getLabel();
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
