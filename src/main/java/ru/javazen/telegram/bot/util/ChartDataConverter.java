package ru.javazen.telegram.bot.util;

import org.springframework.stereotype.Component;
import ru.javazen.telegram.bot.datasource.model.ChartData;
import ru.javazen.telegram.bot.datasource.model.PeriodUserStatistic;
import ru.javazen.telegram.bot.model.UserEntity;

import java.util.*;
import java.util.function.ToLongFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Component
public class ChartDataConverter {
    private ToLongFunction<PeriodUserStatistic> yValueFunc =
            periodUserStatistic -> Math.round(periodUserStatistic.getScore());

    public ChartData convert(List<PeriodUserStatistic> source) {
        Map<UserEntity, Long> totalCounts = source.stream()
                .collect(Collectors.groupingBy(PeriodUserStatistic::getUser, Collectors.summingLong(yValueFunc)));

        List<UserEntity> users = source.stream().map(PeriodUserStatistic::getUser)
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
                .map(entry -> formatDataRow(entry, users))
                .toArray(Object[][]::new);
        target.setData(data);
        return target;
    }

    private Object[] formatDataRow(Map.Entry<String, List<PeriodUserStatistic>> entry, List<UserEntity> users) {
        List<PeriodUserStatistic> statistic = entry.getValue();
        Object[] result = new Object[users.size() + 1];
        Arrays.fill(result, 0);
        result[0] = entry.getKey();
        for (PeriodUserStatistic userStatistic : statistic) {
            int index = users.indexOf(userStatistic.getUser());
            result[1 + index] = yValueFunc.applyAsLong(userStatistic);
        }
        return result;
    }

    private String formatUserName(UserEntity userEntity) {
        return Stream.of(userEntity.getFirstName(), userEntity.getLastName())
                .filter(Objects::nonNull)
                .collect(Collectors.joining(" "));
    }
}
