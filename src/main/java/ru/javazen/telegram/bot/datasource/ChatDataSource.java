package ru.javazen.telegram.bot.datasource;

import ru.javazen.telegram.bot.datasource.model.CountStatistic;
import ru.javazen.telegram.bot.datasource.model.PeriodUserStatistic;
import ru.javazen.telegram.bot.datasource.model.TimeInterval;
import ru.javazen.telegram.bot.datasource.model.UserStatistic;
import ru.javazen.telegram.bot.util.DateRange;

import java.util.List;
import java.util.TimeZone;

public interface ChatDataSource {
    List<UserStatistic> topActiveUsers(Long chatId, DateRange dateRange);

    List<PeriodUserStatistic> activityChart(Long chatId, DateRange dateRange, TimeInterval interval, TimeZone timeZone);

    List<CountStatistic> topStickers(Long chatId, DateRange dateRange, Integer maxResults);

    List<CountStatistic> botUsagesByModule(Long chatId, DateRange dateRange);

    List<CountStatistic> wordsUsageStatistic(Long chatId, DateRange dateRange);

    Long messagesCount(Long chatId, DateRange dateRange);
}
