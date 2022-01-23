package ru.javazen.telegram.bot.datasource;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.javazen.telegram.bot.datasource.model.*;
import ru.javazen.telegram.bot.datasource.query.*;
import ru.javazen.telegram.bot.model.ChatEntity;
import ru.javazen.telegram.bot.util.DateRange;

import java.util.Date;
import java.util.List;

@Repository
@AllArgsConstructor
public class UserStatisticDataSource implements StatisticDataSource<ChatEntity> {
    private final MemberActivityTableQuery activityTableQuery;
    private final MemberActivityTrendChartQuery activityTrendChartQuery;
    private final MemberActivityBarChartQuery activityBarChartQuery;
    private final TopUsedStickerQuery topUsedStickersQuery;
    private final MessageTypesQuery messageTypesQuery;
    private final MessageCountQuery messageCountQuery;

    @Override
    public List<MessageStatistic<ChatEntity>> topActivity(Long userId, DateRange dateRange) {
        return activityTableQuery.getUserActivity(userId, dateRange);
    }

    @Override
    public List<TimestampMessageStatistic<ChatEntity>> activityTrend(Long userId, DateRange dateRange, TimeInterval interval) {
        return activityTrendChartQuery.getUserActivity(userId, dateRange, interval);
    }

    @Override
    public List<PeriodIdMessageStatistic<ChatEntity>> activityBar(Long chatId, DateRange dateRange, TimeGroup periodType) {
        return activityBarChartQuery.userPeriodsChart(chatId, dateRange, periodType);
    }

    @Override
    public List<SubjectCount<String>> topUsedStickers(Long userId, DateRange dateRange, Integer maxResults) {
        return topUsedStickersQuery.getTopUsedUserStickers(userId, dateRange, maxResults);
    }

    @Override
    public List<SubjectCount<String>> messageTypesUsage(Long userId, DateRange dateRange) {
        return messageTypesQuery.getChatMessagesByTypes(userId, dateRange);
    }

    @Override
    public Long messageCountAtDate(Long userId, Date date) {
        return messageCountQuery.getUserMessageCount(userId, date);
    }
}
