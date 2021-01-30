package ru.javazen.telegram.bot.datasource;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.javazen.telegram.bot.datasource.model.CountStatistic;
import ru.javazen.telegram.bot.datasource.model.PeriodStatistic;
import ru.javazen.telegram.bot.datasource.model.Statistic;
import ru.javazen.telegram.bot.datasource.model.TimeInterval;
import ru.javazen.telegram.bot.model.*;
import ru.javazen.telegram.bot.util.DateRange;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.*;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;
import java.util.stream.Collectors;

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
            "  when file_type is not null then file_type " +
            "  else 'TEXT' " +
            "end as type, " +
            "count(*) as count " +
            "from message_entity " +
            "where user_id = :user_id " +
            "  and date between cast(:from as TIMESTAMP) and cast(:to as TIMESTAMP) " +
            "group by type";

    private final EntityManager entityManager;

    @Override
    @Transactional(readOnly = true)
    public List<Statistic.ChatStatistic> topActivity(Long userId, DateRange dateRange) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Statistic.ChatStatistic> query = builder.createQuery(Statistic.ChatStatistic.class);

        Root<MessageEntity> messages = query.from(MessageEntity.class);
        Join<MessageEntity, ChatEntity> chatJoin = messages.join(MessageEntity_.chat);
        query.where(
                builder.equal(messages.get(MessageEntity_.user), userId),
                builder.between(messages.get(MessageEntity_.date), dateRange.getFrom(), dateRange.getTo()));
        query.groupBy(chatJoin);

        Expression<Long> count = builder.count(messages);
        Expression<Long> length = builder.sumAsLong(messages.get(MessageEntity_.textLength));
        Expression<Double> score = builder.sum(messages.get(MessageEntity_.score));
        query.select(builder.construct(Statistic.ChatStatistic.class, chatJoin, count, length, score));
        query.orderBy(builder.desc(score));

        return entityManager.createQuery(query).getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PeriodStatistic.ChatPeriodStatistic> activityChart(Long userId, DateRange dateRange, TimeInterval interval, TimeZone timeZone) {
        Query nativeQuery = entityManager.createNativeQuery(ACTIVITY_CHART_SQL);
        nativeQuery.setParameter("user_id", userId);
        nativeQuery.setParameter("from", dateRange.getFrom());
        nativeQuery.setParameter("to", dateRange.getTo());
        nativeQuery.setParameter("period", interval.getInterval() + " " + interval.getUnit());
        List<Object[]> resultList = nativeQuery.getResultList();
        DateFormat format = new SimpleDateFormat(interval.getUnit().getDatetimeFormat());
        format.setTimeZone(timeZone);
        return resultList.stream().map(obj -> mapToPeriodChatStatistic(obj, format)).collect(Collectors.toList());
    }

    private static PeriodStatistic.ChatPeriodStatistic mapToPeriodChatStatistic(Object[] arr, DateFormat format) {
        String period = format.format((Timestamp) arr[0]);
        if (arr[1] == null) {
            return new PeriodStatistic.ChatPeriodStatistic(period);
        } else {
            long chatId = ((BigInteger) arr[1]).longValue();
            String chatUsername = (String) arr[2];
            String chatTitle = (String) arr[3];
            ChatEntity chat = new ChatEntity(chatId, chatUsername, chatTitle);
            long count = ((BigInteger) arr[4]).longValue();
            long length = ((BigInteger) arr[5]).longValue();
            double score = (Double) arr[6];
            return new PeriodStatistic.ChatPeriodStatistic(period, chat, count, length, score);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<CountStatistic> topStickers(Long userId, DateRange dateRange, Integer maxResults) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<CountStatistic> query = builder.createQuery(CountStatistic.class);

        Root<MessageEntity> messages = query.from(MessageEntity.class);
        query.where(
                builder.equal(messages.get(MessageEntity_.fileType), FileType.STICKER),
                builder.equal(messages.get(MessageEntity_.user), userId),
                builder.between(messages.get(MessageEntity_.date), dateRange.getFrom(), dateRange.getTo()));
        Path<String> fileUniqueId = messages.get(MessageEntity_.fileUniqueId);
        query.groupBy(fileUniqueId);

        Expression<String> fileId = builder.greatest(messages.get(MessageEntity_.fileId));
        Expression<Long> count = builder.count(messages);
        query.select(builder.construct(CountStatistic.class, fileId, count));
        query.orderBy(builder.desc(count));

        return entityManager.createQuery(query)
                .setMaxResults(Optional.ofNullable(maxResults).orElse(5))
                .getResultList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<CountStatistic> messageTypesChart(Long userId, DateRange dateRange) {
        List<Object[]> resultList = entityManager.createNativeQuery(MESSAGE_TYPES_SQL)
                .setParameter("user_id", userId)
                .setParameter("from", dateRange.getFrom())
                .setParameter("to", dateRange.getTo())
                .getResultList();

        return resultList.stream()
                .map(arr -> new CountStatistic((String) arr[0], ((BigInteger) arr[1]).longValue()))
                .sorted(Comparator.comparing(CountStatistic::getKey))
                .collect(Collectors.toList());
    }
}
