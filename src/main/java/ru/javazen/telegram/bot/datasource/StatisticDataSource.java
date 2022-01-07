package ru.javazen.telegram.bot.datasource;

import ru.javazen.telegram.bot.datasource.model.MessageStatistic;
import ru.javazen.telegram.bot.datasource.model.PeriodMessageStatistic;
import ru.javazen.telegram.bot.datasource.model.BaseCount;
import ru.javazen.telegram.bot.datasource.model.TimeInterval;
import ru.javazen.telegram.bot.util.DateRange;

import java.util.Date;
import java.util.List;

public interface StatisticDataSource<T> {
    List<MessageStatistic<T>> topActivity(Long chatId, DateRange dateRange);

    List<PeriodMessageStatistic<T>> activityChart(Long chatId, DateRange dateRange, TimeInterval interval);

    List<BaseCount<String>> topUsedStickers(Long chatId, DateRange dateRange, Integer maxResults);

    List<BaseCount<String>> messageTypesUsage(Long chatId, DateRange dateRange);

    Long messageCountAtDate(Long chatId, Date date);
}
