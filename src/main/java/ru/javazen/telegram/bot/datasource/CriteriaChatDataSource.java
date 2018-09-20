package ru.javazen.telegram.bot.datasource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.javazen.telegram.bot.datasource.model.UserStatistic;
import ru.javazen.telegram.bot.model.MessageEntity;
import ru.javazen.telegram.bot.model.MessageEntity_;
import ru.javazen.telegram.bot.model.UserEntity;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import java.util.Date;
import java.util.List;

@Repository
public class CriteriaChatDataSource implements ChatDataSource {
    private EntityManager entityManager;

    @Autowired
    public CriteriaChatDataSource(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<UserStatistic> topActiveUsers(Long chatId, Date after, Date before) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<UserStatistic> query = builder.createQuery(UserStatistic.class);

        Root<MessageEntity> messages = query.from(MessageEntity.class);
        Join<MessageEntity, UserEntity> userJoin = messages.join(MessageEntity_.user);
        query.where(
                builder.equal(messages.get(MessageEntity_.chat), chatId),
                builder.between(messages.get(MessageEntity_.date), after, before));
        query.groupBy(messages.get(MessageEntity_.user));

        Expression<Long> count = builder.count(messages);
        Expression<Long> length = builder.sum(builder.toLong(builder.length(messages.get(MessageEntity_.text))));
        query.select(builder.construct(UserStatistic.class, userJoin, count, length));
        query.orderBy(builder.desc(count));

        return entityManager.createQuery(query).getResultList();
    }
}
