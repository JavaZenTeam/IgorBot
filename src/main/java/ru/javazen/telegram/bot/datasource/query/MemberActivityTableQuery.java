package ru.javazen.telegram.bot.datasource.query;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.javazen.telegram.bot.datasource.model.MessageStatistic;
import ru.javazen.telegram.bot.model.ChatEntity;
import ru.javazen.telegram.bot.model.MessageEntity;
import ru.javazen.telegram.bot.model.MessageEntity_;
import ru.javazen.telegram.bot.model.UserEntity;
import ru.javazen.telegram.bot.util.DateRange;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.*;
import jakarta.persistence.metamodel.SingularAttribute;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MemberActivityTableQuery {
    private final EntityManager entityManager;

    @Transactional(readOnly = true)
    public List<MessageStatistic<UserEntity>> getChatActivity(Long chatId, DateRange dateRange) {
        return getActivity(MessageEntity_.user, MessageEntity_.chat, chatId, dateRange);
    }

    @Transactional(readOnly = true)
    public List<MessageStatistic<ChatEntity>> getUserActivity(Long userId, DateRange dateRange) {
        return getActivity(MessageEntity_.chat, MessageEntity_.user, userId, dateRange);
    }

    private <T> List<MessageStatistic<T>> getActivity(SingularAttribute<MessageEntity, T> subjectAttr,
                                                      SingularAttribute<MessageEntity, ?> objectAttr,
                                                      Long objectId,
                                                      DateRange dateRange) {
        Class<MessageStatistic<T>> statisticClass = QueryUtils.messageCountFor(subjectAttr.getBindableJavaType());

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<MessageStatistic<T>> query = builder.createQuery(statisticClass);

        Root<MessageEntity> messages = query.from(MessageEntity.class);
        Predicate datePredicate = builder.between(messages.get(MessageEntity_.date), dateRange.getFrom(), dateRange.getTo());
        query.where(builder.equal(messages.get(objectAttr), objectId), datePredicate);

        Join<MessageEntity, ?> subjectJoin = messages.join(subjectAttr);
        query.groupBy(subjectJoin);

        Expression<Long> count = builder.count(messages);
        Expression<Long> length = builder.sumAsLong(messages.get(MessageEntity_.textLength));
        Expression<Double> score = builder.sum(messages.get(MessageEntity_.score));
        query.select(builder.construct(statisticClass, subjectJoin, count, length, score));
        query.orderBy(builder.desc(score));

        List<MessageStatistic<T>> dataset = entityManager.createQuery(query).getResultList();
        dataset.forEach(item -> item.setDataset(dataset));
        return dataset;
    }
}
