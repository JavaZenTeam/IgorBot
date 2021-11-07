package ru.javazen.telegram.bot.datasource.query;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.javazen.telegram.bot.datasource.model.Statistic;
import ru.javazen.telegram.bot.model.ActivityLevel;
import ru.javazen.telegram.bot.util.DateRange;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ActivityLevelsTableQuery {

    private final EntityManager entityManager;

    private static final String SQL_TEMPLATE = "SELECT {0}, " +
            "1.0 * sum(count) / (cast(:to as DATE) - cast(:from as DATE)) " +
            "FROM daily_user_chat_statistic " +
            "WHERE date >= :from and date < :to " +
            "GROUP BY {0}";

    public List<Statistic<ActivityLevel>> getChatActivityByLevels(DateRange dateRange) {
        return getActivityByLevels(dateRange, "chat_id");
    }

    public List<Statistic<ActivityLevel>> getUserActivityByLevels(DateRange dateRange) {
        return getActivityByLevels(dateRange, "user_id");
    }

    private List<Statistic<ActivityLevel>> getActivityByLevels(DateRange dateRange, String groupEntity) {
        Query nativeQuery = entityManager.createNativeQuery(MessageFormat.format(SQL_TEMPLATE, groupEntity))
                .setParameter("from", dateRange.getFrom())
                .setParameter("to", dateRange.getTo());

        List<Double> dailyCounts = QueryUtils.getResultList(nativeQuery,
                (arr -> Optional.ofNullable(arr[1])
                        .map(BigDecimal.class::cast)
                        .map(BigDecimal::doubleValue)
                        .orElse(0d)));

        return Arrays.stream(ActivityLevel.values())
                .sorted()
                .map(activityLevel -> {
                    var count = dailyCounts.stream()
                            .filter(dailyCount -> dailyCount >= activityLevel.getLowerThreshold())
                            .filter(dailyCount -> activityLevel.getUpperThreshold() == null
                                    || dailyCount >= activityLevel.getLowerThreshold())
                            .count();
                    return new Statistic<>(activityLevel, count);
                })
                .collect(Collectors.toList());
    }
}
