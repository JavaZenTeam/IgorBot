package ru.javazen.telegram.bot.datasource;

import ru.javazen.telegram.bot.datasource.model.*;
import ru.javazen.telegram.bot.util.DateRange;

import java.util.Date;
import java.util.List;

public interface StatisticDataSource<T> {
    List<MessageStatistic<T>> topActivity(Long chatId, DateRange dateRange);

    List<TimestampMessageStatistic<T>> activityTrend(Long chatId, DateRange dateRange, TimeInterval interval);

    List<PeriodIdMessageStatistic<T>> activityBar(Long chatId, DateRange dateRange, TimeGroup periodType);

    List<SubjectCount<String>> topUsedStickers(Long chatId, DateRange dateRange, Integer maxResults);

    List<SubjectCount<String>> messageTypesUsage(Long chatId, DateRange dateRange);

    Long messageCountAtDate(Long chatId, Date date);
}
