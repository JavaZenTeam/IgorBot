package ru.javazen.telegram.bot.datasource;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.javazen.telegram.bot.datasource.model.MessageStatistic;
import ru.javazen.telegram.bot.datasource.model.PeriodMessageStatistic;
import ru.javazen.telegram.bot.datasource.model.BaseCount;
import ru.javazen.telegram.bot.datasource.model.TimeInterval;
import ru.javazen.telegram.bot.datasource.query.*;
import ru.javazen.telegram.bot.model.ChatEntity;
import ru.javazen.telegram.bot.util.DateRange;

import java.util.Date;
import java.util.List;

@Repository
@AllArgsConstructor
public class UserStatisticDataSource implements StatisticDataSource<ChatEntity> {
    private static final String ACTIVITY_CHART_SQL = "select generate_series, " +
            "c.chat_id, c.username, c.title, " +
            "count(*) as count, sum(text_length) as length, sum(score) as score " +
            "from generate_series(cast(:from as TIMESTAMP), cast(:to as TIMESTAMP), cast(:period as INTERVAL)) " +
            "left join message_entity m on user_id = :user_id " +
            "and date >= generate_series and date < generate_series + cast(:period as INTERVAL) " +
            "left join chat_entity c on m.chat_id = c.chat_id " +
            "group by generate_series, c.chat_id, c.username, c.title";

    private static final String MESSAGE_TYPES_SQL = "select " +
            "case " +
            "  when forward_user_id is not null then 'FORWARD' " +
            "  when event_type is not null then 'EVENT' " +
            "  when file_type is not null then file_type " +
            "  else 'TEXT' " +
            "end as type, " +
            "count(*) as count " +
            "from message_entity " +
            "where user_id = :user_id " +
            "  and date between cast(:from as TIMESTAMP) and cast(:to as TIMESTAMP) " +
            "group by type";

    private final MemberActivityTableQuery activityTableQuery;
    private final MemberActivityChartQuery activityChartQuery;
    private final TopUsedStickerQuery topUsedStickersQuery;
    private final MessageTypesQuery messageTypesQuery;
    private final MessageCountQuery messageCountQuery;

    @Override
    public List<MessageStatistic<ChatEntity>> topActivity(Long userId, DateRange dateRange) {
        return activityTableQuery.getUserActivity(userId, dateRange);
    }

    @Override
    public List<PeriodMessageStatistic<ChatEntity>> activityChart(Long userId, DateRange dateRange, TimeInterval interval) {
        return activityChartQuery.getUserActivity(userId, dateRange, interval);
    }

    @Override
    public List<MessageStatistic<String>> topUsedStickers(Long userId, DateRange dateRange, Integer maxResults) {
        return topUsedStickersQuery.getTopUsedUserStickers(userId, dateRange, maxResults);
    }

    @Override
    public List<BaseCount<String>> messageTypesUsage(Long userId, DateRange dateRange) {
        return messageTypesQuery.getChatMessagesByTypes(userId, dateRange);
    }

    @Override
    public Integer messageCountAtDate(Long userId, Date date) {
        return messageCountQuery.getUserMessageCount(userId, date);
    }
}
