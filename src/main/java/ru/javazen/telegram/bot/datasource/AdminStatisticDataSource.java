package ru.javazen.telegram.bot.datasource;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.javazen.telegram.bot.datasource.model.PeriodStatistic;
import ru.javazen.telegram.bot.datasource.model.Statistic;
import ru.javazen.telegram.bot.datasource.model.TimeInterval;
import ru.javazen.telegram.bot.datasource.query.ActivityLevelsChartQuery;
import ru.javazen.telegram.bot.datasource.query.ActivityLevelsTableQuery;
import ru.javazen.telegram.bot.model.ActivityLevel;
import ru.javazen.telegram.bot.util.DateRange;

import java.util.List;

@Repository
@AllArgsConstructor
public class AdminStatisticDataSource {
    private final ActivityLevelsChartQuery activityLevelsChartQuery;
    private final ActivityLevelsTableQuery activityLevelsTableQuery;

    public List<Statistic<ActivityLevel>> chatActivityByLevels(DateRange dateRange) {
        return activityLevelsTableQuery.getChatActivityByLevels(dateRange);
    }

    public List<Statistic<ActivityLevel>> userActivityByLevels(DateRange dateRange) {
        return activityLevelsTableQuery.getUserActivityByLevels(dateRange);
    }

    public List<PeriodStatistic<ActivityLevel>> chatActivityChartByLevels(DateRange dateRange, TimeInterval interval) {
        return activityLevelsChartQuery.getChatActivityChartByLevels(dateRange, interval);
    }

    public List<PeriodStatistic<ActivityLevel>> userActivityChartByLevels(DateRange dateRange, TimeInterval interval) {
        return activityLevelsChartQuery.getUserActivityChartByLevels(dateRange, interval);
    }
}
