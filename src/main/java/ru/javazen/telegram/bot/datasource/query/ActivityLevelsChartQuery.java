package ru.javazen.telegram.bot.datasource.query;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.javazen.telegram.bot.datasource.model.PeriodStatistic;
import ru.javazen.telegram.bot.datasource.model.TimeInterval;
import ru.javazen.telegram.bot.model.ActivityLevel;
import ru.javazen.telegram.bot.util.DateRange;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ActivityLevelsChartQuery {
    private final EntityManager entityManager;

    private static final String SQL_TEMPLATE = "SELECT {0}, " +
            "generate_series, " +
            "1.0 * count(message_id) / (extract(epoch from cast(:period as INTERVAL)) / 86400) " +
            "FROM generate_series(cast(:from as TIMESTAMP), cast(:to as TIMESTAMP), cast(:period as INTERVAL)) " +
            "LEFT JOIN message_entity " +
            " ON date >= generate_series " +
            " AND date < generate_series + cast(:period as INTERVAL) " +
            "GROUP BY generate_series, {0}";

    public List<PeriodStatistic<ActivityLevel>> getChatActivityChartByLevels(DateRange dateRange, TimeInterval interval) {
        return activityChartByLevels(dateRange, interval, "chat_id");
    }

    public List<PeriodStatistic<ActivityLevel>> getUserActivityChartByLevels(DateRange dateRange, TimeInterval interval) {
        return activityChartByLevels(dateRange, interval, "user_id");
    }

    private List<PeriodStatistic<ActivityLevel>> activityChartByLevels(DateRange dateRange, TimeInterval interval, String groupEntity) {

        Query nativeQuery = entityManager.createNativeQuery(MessageFormat.format(SQL_TEMPLATE, groupEntity))
                .setParameter("from", dateRange.getFrom())
                .setParameter("to", dateRange.getTo())
                .setParameter("period", interval.getQuantity() + " " + interval.getUnit());

        Map<Timestamp, List<Double>> dailyCounts = QueryUtils.getResultStream(nativeQuery)
                .collect(Collectors.groupingBy((arr -> (Timestamp) arr[1]),
                        Collectors.mapping(arr -> arr[0] == null ? null : ((Number) arr[2]).doubleValue(),
                                Collectors.toList())));

        return Arrays.stream(ActivityLevel.values())
                .flatMap(activityLevel -> dailyCounts.entrySet().stream()
                        .map(dailyCountEntry -> {
                            var count = dailyCountEntry.getValue().stream()
                                    .filter(Objects::nonNull)
                                    .filter(dailyCount -> dailyCount >= activityLevel.getLowerThreshold())
                                    .filter(dailyCount -> activityLevel.getUpperThreshold() == null
                                            || dailyCount >= activityLevel.getLowerThreshold())
                                    .count();
                            return new PeriodStatistic<>(dailyCountEntry.getKey(), activityLevel, count);
                        }))
                .collect(Collectors.toList());
    }
}
