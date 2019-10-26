package ru.javazen.telegram.bot.datasource;

import ru.javazen.telegram.bot.datasource.model.*;
import ru.javazen.telegram.bot.util.DateRange;

import java.util.List;
import java.util.TimeZone;

public interface ChatDataSource {
    List<UserStatistic> topActiveUsers(Long chatId, DateRange dateRange);

    List<PeriodUserStatistic> activityChart(Long chatId, DateRange dateRange, TimeInterval interval, TimeZone timeZone);

    List<CountStatistic> topStickers(Long chatId, DateRange dateRange, Integer maxResults);

    List<CountStatistic> messageTypesStickers(Long chatId, DateRange dateRange);

    List<CountStatistic> botUsagesByModule(Long chatId, DateRange dateRange);

    DataTableResponse<WordUsageStatistic> wordsUsageStatistic(Long chatId, DateRange dateRange, DataTableRequest dataTableRequest);

    Long messagesCount(Long chatId, DateRange dateRange);
}
