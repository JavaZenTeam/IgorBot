package ru.javazen.telegram.bot.datasource;

import ru.javazen.telegram.bot.datasource.model.CountStatistic;
import ru.javazen.telegram.bot.datasource.model.PeriodStatistic;
import ru.javazen.telegram.bot.datasource.model.Statistic;
import ru.javazen.telegram.bot.datasource.model.TimeInterval;
import ru.javazen.telegram.bot.util.DateRange;

import java.util.List;
import java.util.TimeZone;

public interface StatisticDataSource<T> {
    List<? extends Statistic<? extends T>> topActivity(Long chatId, DateRange dateRange);

    List<? extends PeriodStatistic<? extends T>> activityChart(Long chatId, DateRange dateRange, TimeInterval interval, TimeZone timeZone);

    List<CountStatistic> topStickers(Long chatId, DateRange dateRange, Integer maxResults);

    List<CountStatistic> messageTypesChart(Long chatId, DateRange dateRange);
}