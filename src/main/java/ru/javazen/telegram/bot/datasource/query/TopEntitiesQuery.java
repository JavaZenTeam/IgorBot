package ru.javazen.telegram.bot.datasource.query;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.javazen.telegram.bot.datasource.model.SubjectCount;
import ru.javazen.telegram.bot.model.ChatEntity;
import ru.javazen.telegram.bot.model.MessageEntity;
import ru.javazen.telegram.bot.model.MessageEntity_;
import ru.javazen.telegram.bot.model.UserEntity;
import ru.javazen.telegram.bot.util.DateRange;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import javax.persistence.metamodel.SingularAttribute;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TopEntitiesQuery {
    private final EntityManager entityManager;

    public List<SubjectCount<UserEntity>> topUsers(DateRange dateRange, Integer maxResults) {
        return getTop(MessageEntity_.user, dateRange, maxResults);

    }

    public List<SubjectCount<ChatEntity>> topChats(DateRange dateRange, Integer maxResults) {
        return getTop(MessageEntity_.chat, dateRange, maxResults);
    }


    private <T> List<SubjectCount<T>> getTop(SingularAttribute<MessageEntity, T> subjectAttr,
                                                      DateRange dateRange, Integer maxResults) {
        Class<SubjectCount<T>> statisticClass = QueryUtils.countFor(subjectAttr.getBindableJavaType());

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<SubjectCount<T>> query = builder.createQuery(statisticClass);

        Root<MessageEntity> messages = query.from(MessageEntity.class);
        query.where(builder.between(messages.get(MessageEntity_.date), dateRange.getFrom(), dateRange.getTo()));

        Join<MessageEntity, ?> subjectJoin = messages.join(subjectAttr);
        query.groupBy(subjectJoin);

        Expression<Long> count = builder.count(messages);
        query.select(builder.construct(statisticClass, subjectJoin, count));
        query.orderBy(builder.desc(count));

        return entityManager.createQuery(query).getResultList();
    }
}
