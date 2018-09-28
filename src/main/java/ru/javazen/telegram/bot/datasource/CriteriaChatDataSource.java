package ru.javazen.telegram.bot.datasource;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.javazen.telegram.bot.datasource.model.BotUsageStatistic;
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
    public List<BotUsageStatistic> botUsagesByModule(Long chatId, Date after, Date before) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<BotUsageStatistic> query = builder.createQuery(BotUsageStatistic.class);

        Root<BotUsageLog> botUsages = query.from(BotUsageLog.class);
        Join<BotUsageLog, MessageEntity> messageJoin = botUsages.join(BotUsageLog_.source);

        query.where(
                builder.equal(messageJoin.get(MessageEntity_.chat), chatId),
                builder.between(messageJoin.get(MessageEntity_.date), after, before));
        query.groupBy(botUsages.get(BotUsageLog_.moduleName));

        Expression<Long> count = builder.count(botUsages);
        query.select(builder.construct(BotUsageStatistic.class, botUsages.get(BotUsageLog_.moduleName), count));
        query.orderBy(builder.desc(count));
        return entityManager.createQuery(query).getResultList();
    }
}
