package ru.javazen.telegram.bot.datasource;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.javazen.telegram.bot.datasource.model.CountStatistic;
import ru.javazen.telegram.bot.datasource.model.PeriodUserStatistic;
import ru.javazen.telegram.bot.datasource.model.TimeInterval;
import ru.javazen.telegram.bot.datasource.model.UserStatistic;
import ru.javazen.telegram.bot.model.*;
import ru.javazen.telegram.bot.service.ChatConfigService;
import ru.javazen.telegram.bot.util.DateRange;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.*;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;
import java.util.stream.Collectors;

@Repository
@AllArgsConstructor
public class CriteriaChatDataSource implements ChatDataSource {
    private static final String TIMEZONE_OFFSET_CONFIG_KEY = "TIMEZONE_OFFSET";
    private static final String ACTIVITY_CHART_SQL = "select generate_series, " +
            "u.user_id, u.first_name, u.last_name, u.username, " +
            "count(*) as count, sum(text_length) as length, sum(score) as score " +
            "from generate_series(cast(:from as TIMESTAMP), cast(:to as TIMESTAMP), cast(:period as INTERVAL)) " +
            "left join message_entity m on chat_id = :chat_id " +
            "and date >= generate_series and date < generate_series + cast(:period as INTERVAL) " +
            "left join user_entity u on m.user_id = u.user_id " +
            "group by generate_series, u.user_id, u.first_name, u.last_name, u.username";

    private EntityManager entityManager;
    private ChatConfigService chatConfigService;

    @Override
    public List<UserStatistic> topActiveUsers(Long chatId, DateRange dateRange) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<UserStatistic> query = builder.createQuery(UserStatistic.class);

        Root<MessageEntity> messages = query.from(MessageEntity.class);
        Join<MessageEntity, UserEntity> userJoin = messages.join(MessageEntity_.user);
        query.where(
                builder.equal(messages.get(MessageEntity_.chat), chatId),
                builder.between(messages.get(MessageEntity_.date), dateRange.getFrom(), dateRange.getTo()));
        query.groupBy(userJoin);

        Expression<Long> count = builder.count(messages);
        Expression<Long> length = builder.sumAsLong(messages.get(MessageEntity_.textLength));
        Expression<Double> score = builder.sum(messages.get(MessageEntity_.score));
        query.select(builder.construct(UserStatistic.class, userJoin, count, length, score));
        query.orderBy(builder.desc(score));

        return entityManager.createQuery(query).getResultList();
    }

    @Override
    public List<PeriodUserStatistic> activityChart(Long chatId, DateRange dateRange, TimeInterval interval, TimeZone timeZone) {
        Query nativeQuery = entityManager.createNativeQuery(ACTIVITY_CHART_SQL);
        nativeQuery.setParameter("chat_id", chatId);
        nativeQuery.setParameter("from", dateRange.getFrom());
        nativeQuery.setParameter("to", dateRange.getTo());
        nativeQuery.setParameter("period", interval.getInterval() + " " + interval.getUnit());
        List<Object[]> resultList = nativeQuery.getResultList();
        DateFormat format = new SimpleDateFormat(resolveDateTimeFormat(interval.getUnit()));
        format.setTimeZone(timeZone);
        return resultList.stream().map(obj -> mapToPeriodUserStatistic(obj, format)).collect(Collectors.toList());
    }

    private static String resolveDateTimeFormat(TimeInterval.Unit unit) {
        switch (unit) {
            case YEAR:
                return "YYYY";
            case MONTH:
                return "YYYY-MM";
            case DAY:
                return "YYYY-MM-dd";
            case HOUR:
            default:
                return "YYYY-MM-dd HH:mm";
        }
    }

    private static PeriodUserStatistic mapToPeriodUserStatistic(Object[] arr, DateFormat format) {
        String period = format.format((Timestamp) arr[0]);
        if (arr[1] == null) {
            return new PeriodUserStatistic(period);
        } else {
            UserEntity user = new UserEntity((Integer) arr[1], (String) arr[2], (String) arr[3], (String) arr[4]);
            long count = ((BigInteger) arr[5]).longValue();
            long length = ((BigInteger) arr[6]).longValue();
            double score = (Double) arr[7];
            return new PeriodUserStatistic(period, user, count, length, score);
        }
    }

    @Override
    public List<CountStatistic> topStickers(Long chatId, DateRange dateRange, Integer maxResults) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<CountStatistic> query = builder.createQuery(CountStatistic.class);

        Root<MessageEntity> messages = query.from(MessageEntity.class);
        query.where(
                builder.equal(messages.get(MessageEntity_.fileType), FileType.STICKER),
                builder.equal(messages.get(MessageEntity_.chat), chatId),
                builder.between(messages.get(MessageEntity_.date), dateRange.getFrom(), dateRange.getTo()));
        Path<String> fileId = messages.get(MessageEntity_.fileId);
        query.groupBy(fileId);

        Expression<Long> count = builder.count(messages);
        query.select(builder.construct(CountStatistic.class, fileId, count));
        query.orderBy(builder.desc(count));

        return entityManager.createQuery(query)
                .setMaxResults(Optional.ofNullable(maxResults).orElse(5))
                .getResultList();
    }

    @Override
    public List<CountStatistic> botUsagesByModule(Long chatId, DateRange dateRange) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<CountStatistic> query = builder.createQuery(CountStatistic.class);

        Root<BotUsageLog> botUsages = query.from(BotUsageLog.class);
        Join<BotUsageLog, MessageEntity> messageJoin = botUsages.join(BotUsageLog_.source);

        query.where(
                builder.equal(messageJoin.get(MessageEntity_.chat), chatId),
                builder.between(messageJoin.get(MessageEntity_.date), dateRange.getFrom(), dateRange.getTo()));
        query.groupBy(botUsages.get(BotUsageLog_.moduleName));

        Expression<Long> count = builder.count(botUsages);
        query.select(builder.construct(CountStatistic.class, botUsages.get(BotUsageLog_.moduleName), count));
        query.orderBy(builder.desc(count));
        return entityManager.createQuery(query).getResultList();
    }

    @Override
    public List<CountStatistic> wordsUsageStatistic(Long chatId, DateRange dateRange) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<CountStatistic> query = builder.createQuery(CountStatistic.class);

        Root<MessageEntity> messages = query.from(MessageEntity.class);
        Join<MessageEntity, String> wordJoin = messages.join(MessageEntity_.words);

        query.where(
                builder.greaterThanOrEqualTo(builder.length(wordJoin.as(String.class)), 3),
                builder.equal(messages.get(MessageEntity_.chat), chatId),
                builder.between(messages.get(MessageEntity_.date), dateRange.getFrom(), dateRange.getTo()));
        query.groupBy(wordJoin);

        Expression<Long> count = builder.count(messages);
        query.select(builder.construct(CountStatistic.class, wordJoin, count));
        query.orderBy(builder.desc(count));
        return entityManager.createQuery(query)
                .setMaxResults(50)
                .getResultList();
    }

    @Override
    public Long messagesCount(Long chatId, DateRange dateRange) {
        TimeZone timeZone = getTimeZone(chatId);
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = builder.createQuery(Long.class);
        Root<MessageEntity> messages = query.from(MessageEntity.class);
        query.where(
                builder.equal(messages.get(MessageEntity_.chat), chatId),
                builder.between(messages.get(MessageEntity_.date), dateRange.getFrom(), dateRange.getTo()));
        query.select(builder.count(messages));
        List<Long> resultList = entityManager.createQuery(query)
                .getResultList();
        return resultList.isEmpty() ? 0L : resultList.get(0);
    }

    private TimeZone getTimeZone(long chatId) {
        String timeZoneOffset = chatConfigService.getProperty(chatId, TIMEZONE_OFFSET_CONFIG_KEY).orElse("+04:00");
        return TimeZone.getTimeZone("GMT" + timeZoneOffset);
    }
}
