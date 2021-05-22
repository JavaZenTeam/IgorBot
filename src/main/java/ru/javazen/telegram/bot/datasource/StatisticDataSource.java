package ru.javazen.telegram.bot.datasource;

import ru.javazen.telegram.bot.datasource.model.PeriodStatistic;
import ru.javazen.telegram.bot.datasource.model.Statistic;
import ru.javazen.telegram.bot.datasource.model.TimeInterval;
import ru.javazen.telegram.bot.util.DateRange;

import java.util.Date;
import java.util.List;

public interface StatisticDataSource<T> {
    List<Statistic<T>> topActivity(Long chatId, DateRange dateRange);

    List<PeriodStatistic<T>> activityChart(Long chatId, DateRange dateRange, TimeInterval interval);

    List<Statistic<String>> topUsedStickers(Long chatId, DateRange dateRange, Integer maxResults);

    List<Statistic<String>> messageTypesUsage(Long chatId, DateRange dateRange);

    Integer messageCountAtDate(Long chatId, Date date);
}
