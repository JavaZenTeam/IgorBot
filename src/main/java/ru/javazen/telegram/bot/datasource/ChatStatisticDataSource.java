package ru.javazen.telegram.bot.datasource;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.javazen.telegram.bot.datasource.model.MessageStatistic;
import ru.javazen.telegram.bot.datasource.model.PeriodMessageStatistic;
import ru.javazen.telegram.bot.datasource.model.SubjectCount;
import ru.javazen.telegram.bot.datasource.model.TimeInterval;
import ru.javazen.telegram.bot.datasource.query.*;
import ru.javazen.telegram.bot.model.UserEntity;
import ru.javazen.telegram.bot.util.DateRange;

import java.util.Date;
import java.util.List;

@Repository
@AllArgsConstructor
public class ChatStatisticDataSource implements StatisticDataSource<UserEntity> {
    private final MemberActivityTableQuery activityTableQuery;
    private final MemberActivityChartQuery activityChartQuery;
    private final TopUsedStickerQuery topUsedStickersQuery;
    private final MessageTypesQuery messageTypesQuery;
    private final MessageCountQuery messageCountQuery;

    @Override
    public List<MessageStatistic<UserEntity>> topActivity(Long chatId, DateRange dateRange) {
        return activityTableQuery.getChatActivity(chatId, dateRange);
    }

    @Override
    public List<PeriodMessageStatistic<UserEntity>> activityChart(Long chatId, DateRange dateRange, TimeInterval interval) {
        return activityChartQuery.getChatActivity(chatId, dateRange, interval);
    }

    @Override
    public List<SubjectCount<String>> topUsedStickers(Long chatId, DateRange dateRange, Integer maxResults) {
        return topUsedStickersQuery.getTopUsedChatStickers(chatId, dateRange, maxResults);
    }

    @Override
    public List<SubjectCount<String>> messageTypesUsage(Long chatId, DateRange dateRange) {
        return messageTypesQuery.getChatMessagesByTypes(chatId, dateRange);
    }

    @Override
    public Long messageCountAtDate(Long chatId, Date date) {
        return messageCountQuery.getChatMessageCount(chatId, date);
    }
}
