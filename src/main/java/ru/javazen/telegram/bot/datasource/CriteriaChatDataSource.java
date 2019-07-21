package ru.javazen.telegram.bot.datasource;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.javazen.telegram.bot.datasource.model.CountStatistic;
import ru.javazen.telegram.bot.datasource.model.PeriodUserStatistic;
import ru.javazen.telegram.bot.datasource.model.UserStatistic;
import ru.javazen.telegram.bot.model.*;
import ru.javazen.telegram.bot.util.DateRange;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class CriteriaChatDataSource implements ChatDataSource {
    private EntityManager entityManager;

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
    public List<PeriodUserStatistic> activityChart(Long chatId, DateRange dateRange) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<PeriodUserStatistic> query = builder.createQuery(PeriodUserStatistic.class);

        Root<MessageEntity> messages = query.from(MessageEntity.class);
        Join<MessageEntity, UserEntity> userJoin = messages.join(MessageEntity_.user);
        query.where(
                builder.equal(messages.get(MessageEntity_.chat), chatId),
                builder.between(messages.get(MessageEntity_.date), dateRange.getFrom(), dateRange.getTo()));
        Expression<String> dateFunction = builder.function("TO_CHAR", String.class,
                messages.get(MessageEntity_.date),
                InlineLiteral.of(builder, resolveDateTimeFormat(dateRange.duration())));
        query.groupBy(dateFunction, userJoin);

        Expression<Long> count = builder.count(messages);
        Expression<Long> length = builder.sumAsLong(messages.get(MessageEntity_.textLength));
        Expression<Double> score = builder.sum(messages.get(MessageEntity_.score));
        query.select(builder.construct(PeriodUserStatistic.class, dateFunction, userJoin, count, length, score));
        return entityManager.createQuery(query).getResultList();
    }

    private String resolveDateTimeFormat(Duration duration) {
        double monthSize = 31;
        double yearSize = 365;
        long days = duration.toDays();
        if (days <= 3) {
            return "YYYY-MM-dd HH24:00";
        } else if (days <= monthSize * 3) {
            return "YYYY-MM-dd";
        } else if (days <= yearSize * 3) {
            return "YYYY-MM";
        } else {
            return "YYYY";
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
}
