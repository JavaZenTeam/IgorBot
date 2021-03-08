package ru.javazen.telegram.bot.datasource;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.javazen.telegram.bot.datasource.model.CountStatistic;
import ru.javazen.telegram.bot.datasource.model.PeriodStatistic;
import ru.javazen.telegram.bot.datasource.model.TimeInterval;
import ru.javazen.telegram.bot.datasource.model.Statistic;
import ru.javazen.telegram.bot.model.*;
import ru.javazen.telegram.bot.util.DateRange;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.*;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@AllArgsConstructor
public class ChatStatisticDataSource implements StatisticDataSource<UserEntity> {

    private static final String ACTIVITY_CHART_SQL = "select generate_series, " +
            "u.user_id, u.first_name, u.last_name, u.username, " +
            "count(*) as count, sum(text_length) as length, sum(score) as score " +
            "from generate_series(cast(:from as TIMESTAMP), cast(:to as TIMESTAMP), cast(:period as INTERVAL)) " +
            "left join message_entity m on chat_id = :chat_id " +
            "and date >= generate_series and date < generate_series + cast(:period as INTERVAL) " +
            "left join user_entity u on m.user_id = u.user_id " +
            "group by generate_series, u.user_id, u.first_name, u.last_name, u.username";

    private static final String MESSAGE_TYPES_SQL = "select " +
            "case " +
            "  when forward_user_id is not null then 'FORWARD' " +
            "  when event_type is not null then 'EVENT' " +
            "  when file_type is not null then file_type " +
            "  else 'TEXT' " +
            "end as type, " +
            "count(*) as count " +
            "from message_entity " +
            "where chat_id = :chat_id " +
            "  and date between cast(:from as TIMESTAMP) and cast(:to as TIMESTAMP) " +
            "group by type";

    private final EntityManager entityManager;

    @Override
    @Transactional(readOnly = true)
    public List<Statistic.UserStatistic> topActivity(Long chatId, DateRange dateRange) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Statistic.UserStatistic> query = builder.createQuery(Statistic.UserStatistic.class);

        Root<MessageEntity> messages = query.from(MessageEntity.class);
        Join<MessageEntity, UserEntity> userJoin = messages.join(MessageEntity_.user);
        query.where(
                builder.equal(messages.get(MessageEntity_.chat), chatId),
                builder.between(messages.get(MessageEntity_.date), dateRange.getFrom(), dateRange.getTo()));
        query.groupBy(userJoin);

        Expression<Long> count = builder.count(messages);
        Expression<Long> length = builder.sumAsLong(messages.get(MessageEntity_.textLength));
        Expression<Double> score = builder.sum(messages.get(MessageEntity_.score));
        query.select(builder.construct(Statistic.UserStatistic.class, userJoin, count, length, score));
        query.orderBy(builder.desc(score));

        List<Statistic.UserStatistic> dataset = entityManager.createQuery(query).getResultList();
        dataset.forEach(item -> item.setDataset(dataset));
        return dataset;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PeriodStatistic.UserPeriodStatistic> activityChart(Long chatId, DateRange dateRange, TimeInterval interval, ZoneId timeZone) {
        Query nativeQuery = entityManager.createNativeQuery(ACTIVITY_CHART_SQL);
        nativeQuery.setParameter("chat_id", chatId);
        nativeQuery.setParameter("from", dateRange.getFrom());
        nativeQuery.setParameter("to", dateRange.getTo());
        nativeQuery.setParameter("period", interval.getQuantity() + " " + interval.getUnit());
        List<Object[]> resultList = nativeQuery.getResultList();
        var dataset = resultList.stream()
                .map(obj -> mapToPeriodUserStatistic(obj, timeZone))
                .collect(Collectors.toList());
        dataset.forEach(item -> item.setDataset(dataset));
        return dataset;
    }

    private static PeriodStatistic.UserPeriodStatistic mapToPeriodUserStatistic(Object[] arr, ZoneId userZone) {
        Timestamp periodTimestamp = (Timestamp) arr[0];
        LocalDateTime periodDateTime = periodTimestamp.toLocalDateTime()
                .atZone(ZoneId.systemDefault())
                .withZoneSameInstant(userZone)
                .toLocalDateTime();
        if (arr[1] == null) {
            return new PeriodStatistic.UserPeriodStatistic(periodDateTime);
        } else {
            UserEntity user = new UserEntity((Integer) arr[1], (String) arr[2], (String) arr[3], (String) arr[4]);
            long count = ((BigInteger) arr[5]).longValue();
            long length = ((BigInteger) arr[6]).longValue();
            double score = (Double) arr[7];
            return new PeriodStatistic.UserPeriodStatistic(periodDateTime, user, count, length, score);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<CountStatistic> topStickers(Long chatId, DateRange dateRange, Integer maxResults) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<CountStatistic> query = builder.createQuery(CountStatistic.class);

        Root<MessageEntity> messages = query.from(MessageEntity.class);
        query.where(
                builder.equal(messages.get(MessageEntity_.fileType), FileType.STICKER),
                builder.equal(messages.get(MessageEntity_.chat), chatId),
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
    public List<CountStatistic> messageTypesChart(Long chatId, DateRange dateRange) {
        List<Object[]> resultList = entityManager.createNativeQuery(MESSAGE_TYPES_SQL)
                .setParameter("chat_id", chatId)
                .setParameter("from", dateRange.getFrom())
                .setParameter("to", dateRange.getTo())
                .getResultList();

        return resultList.stream()
                .map(arr -> new CountStatistic((String) arr[0], ((BigInteger) arr[1]).longValue()))
                .sorted(Comparator.comparing(CountStatistic::getKey))
                .collect(Collectors.toList());
    }

    @Override
    public Integer messageCountByDate(Long chatId, Date date) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Integer> query = builder.createQuery(Integer.class);
        Root<MessageEntity> messages = query.from(MessageEntity.class);
        query.where(
                builder.equal(messages.get(MessageEntity_.chat), chatId),
                builder.lessThanOrEqualTo(messages.get(MessageEntity_.date), date)
        );
        query.select(builder.max(messages.get(MessageEntity_.messagePK).get(MessagePK_.messageId)));
        return Optional.ofNullable(entityManager.createQuery(query).getSingleResult()).orElse(0);
    }
}
