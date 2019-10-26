package ru.javazen.telegram.bot.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Component;
import ru.javazen.telegram.bot.datasource.model.ChartData;
import ru.javazen.telegram.bot.datasource.model.PeriodUserStatistic;
import ru.javazen.telegram.bot.model.UserEntity;

import java.util.*;
import java.util.function.ToLongFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.summingLong;

@Component
public class ChartDataConverter {
    public ChartData convert(List<PeriodUserStatistic> source, Attribute attribute) {
        Map<UserEntity, Long> totalCounts = source.stream()
                .filter(statistic -> statistic.getUser() != null)
                .collect(Collectors.groupingBy(PeriodUserStatistic::getUser, summingLong(attribute.function)));

        List<UserEntity> users = source.stream()
                .map(PeriodUserStatistic::getUser)
                .filter(Objects::nonNull)
                .distinct()
                .sorted(Comparator.<UserEntity, Long>comparing(totalCounts::get).reversed())
                .collect(Collectors.toList());
        ChartData target = new ChartData();
        target.setXKey(0);
        target.setYKeys(IntStream.range(1, users.size() + 1).toArray());
        target.setLabels(users.stream().map(this::formatUserName).toArray(String[]::new));
        Object[][] data = source.stream()
                .collect(Collectors.groupingBy(PeriodUserStatistic::getPeriod))
                .entrySet().stream()
                .map(entry -> formatDataRow(entry, users, attribute))
                .toArray(Object[][]::new);
        target.setData(data);
        return target;
    }

    private Object[] formatDataRow(Map.Entry<String, List<PeriodUserStatistic>> entry,
                                   List<UserEntity> users, Attribute attribute) {
        List<PeriodUserStatistic> statistic = entry.getValue();
        Object[] result = new Object[users.size() + 1];
        Arrays.fill(result, 0);
        result[0] = entry.getKey();
        for (PeriodUserStatistic userStatistic : statistic) {
            if (userStatistic.getUser() != null) {
                int index = users.indexOf(userStatistic.getUser());
                result[1 + index] = attribute.function.applyAsLong(userStatistic);
            }
        }
        return result;
    }

    private String formatUserName(UserEntity userEntity) {
        return Stream.of(userEntity.getFirstName(), userEntity.getLastName())
                .filter(Objects::nonNull)
                .collect(Collectors.joining(" "));
    }

    @AllArgsConstructor
    @Getter
    public enum Attribute {
        MESSAGES(PeriodUserStatistic::getCount),
        CHARACTERS(PeriodUserStatistic::getLength),
        SCORE(periodUserStatistic -> Math.round(periodUserStatistic.getScore()));

        private ToLongFunction<PeriodUserStatistic> function;
    }
}
