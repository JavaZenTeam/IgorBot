package ru.javazen.telegram.bot.util;

import ru.javazen.telegram.bot.datasource.model.ChartData;
import ru.javazen.telegram.bot.datasource.model.PeriodUserStatistic;
import ru.javazen.telegram.bot.model.UserEntity;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class ChartDataUtil {
    private Function<PeriodUserStatistic, Integer> yValueFunc =
            periodUserStatistic -> Math.toIntExact(Math.round(periodUserStatistic.getScore()));

    public ChartData convert(List<PeriodUserStatistic> source) {
        Map<UserEntity, Integer> totalCounts = source.stream()
                .collect(Collectors.groupingBy(PeriodUserStatistic::getUser, Collectors.summingInt(yValueFunc::apply)));

        List<UserEntity> users = source.stream().map(PeriodUserStatistic::getUser)
                .distinct()
                .sorted(Comparator.<UserEntity, Integer>comparing(totalCounts::get).reversed())
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
        result[0] = entry.getKey();
        for (PeriodUserStatistic userStatistic : statistic) {
            int index = users.indexOf(userStatistic.getUser());
            result[1 + index] = yValueFunc.apply(userStatistic);
        }
        return result;
    }

    private String formatUserName(UserEntity userEntity) {
        return Stream.of(userEntity.getFirstName(), userEntity.getLastName())
                .filter(Objects::nonNull)
                .collect(Collectors.joining(" "));
    }
}
