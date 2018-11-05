package ru.javazen.telegram.bot.datasource;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.javazen.telegram.bot.datasource.model.CountStatistic;
import ru.javazen.telegram.bot.datasource.model.UserStatistic;
import ru.javazen.telegram.bot.model.*;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import java.util.Date;
import java.util.List;

@Repository
@AllArgsConstructor
public class CriteriaChatDataSource implements ChatDataSource {
    private EntityManager entityManager;

    @Override
    public List<UserStatistic> topActiveUsers(Long chatId, Date after, Date before) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<UserStatistic> query = builder.createQuery(UserStatistic.class);

        Root<MessageEntity> messages = query.from(MessageEntity.class);
        Join<MessageEntity, UserEntity> userJoin = messages.join(MessageEntity_.user);
        query.where(
                builder.equal(messages.get(MessageEntity_.chat), chatId),
                builder.between(messages.get(MessageEntity_.date), after, before));
        query.groupBy(userJoin);

        Expression<Long> count = builder.count(messages);
        Expression<Long> length = builder.sum(builder.toLong(builder.length(messages.get(MessageEntity_.text))));
        query.select(builder.construct(UserStatistic.class, userJoin, count, length));
        query.orderBy(builder.desc(count));

        return entityManager.createQuery(query).getResultList();
    }

    @Override
    public List<CountStatistic> botUsagesByModule(Long chatId, Date after, Date before) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<CountStatistic> query = builder.createQuery(CountStatistic.class);

        Root<BotUsageLog> botUsages = query.from(BotUsageLog.class);
        Join<BotUsageLog, MessageEntity> messageJoin = botUsages.join(BotUsageLog_.source);

        query.where(
                builder.equal(messageJoin.get(MessageEntity_.chat), chatId),
                builder.between(messageJoin.get(MessageEntity_.date), after, before));
        query.groupBy(botUsages.get(BotUsageLog_.moduleName));

        Expression<Long> count = builder.count(botUsages);
        query.select(builder.construct(CountStatistic.class, botUsages.get(BotUsageLog_.moduleName), count));
        query.orderBy(builder.desc(count));
        return entityManager.createQuery(query).getResultList();
    }

    @Override
    public List<CountStatistic> wordsUsageStatistic(Long chatId, Date after, Date before) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<CountStatistic> query = builder.createQuery(CountStatistic.class);

        Root<MessageEntity> messages = query.from(MessageEntity.class);
        Join<MessageEntity, String> wordJoin = messages.join(MessageEntity_.words);

        query.where(
                builder.equal(messages.get(MessageEntity_.chat), chatId),
                builder.between(messages.get(MessageEntity_.date), after, before));
        query.groupBy(wordJoin);

        Expression<Long> count = builder.count(messages);
        query.select(builder.construct(CountStatistic.class, wordJoin, count));
        query.orderBy(builder.desc(count));
        return entityManager.createQuery(query)
                .setMaxResults(50)
                .getResultList();
    }

    @Override
    public Long messagesCount(Long chatId, Date after, Date before) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = builder.createQuery(Long.class);
        Root<MessageEntity> messages = query.from(MessageEntity.class);
        query.where(
                builder.equal(messages.get(MessageEntity_.chat), chatId),
                builder.between(messages.get(MessageEntity_.date), after, before));
        query.select(builder.count(messages));
        List<Long> resultList = entityManager.createQuery(query)
                .getResultList();
        return resultList.isEmpty() ? 0L : resultList.get(0);
    }
}
