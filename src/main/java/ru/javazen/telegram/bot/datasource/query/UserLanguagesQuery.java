package ru.javazen.telegram.bot.datasource.query;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.javazen.telegram.bot.datasource.model.SubjectCount;
import ru.javazen.telegram.bot.model.MessageEntity;
import ru.javazen.telegram.bot.model.MessageEntity_;
import ru.javazen.telegram.bot.model.UserEntity;
import ru.javazen.telegram.bot.model.UserEntity_;
import ru.javazen.telegram.bot.util.DateRange;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.*;
import java.util.List;

@Component
@RequiredArgsConstructor
public class UserLanguagesQuery {

    private final EntityManager entityManager;

    public List<SubjectCount<String>> getLanguages(DateRange dateRange) {
        Class<SubjectCount<String>> statisticClass = QueryUtils.countFor(String.class);

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<SubjectCount<String>> query = builder.createQuery(statisticClass);

        Root<MessageEntity> messages = query.from(MessageEntity.class);
        Join<MessageEntity, UserEntity> userJoin = messages.join(MessageEntity_.user);

        query.where(builder.between(messages.get(MessageEntity_.date), dateRange.getFrom(), dateRange.getTo()));
        Path<String> languagePath = userJoin.get(UserEntity_.languageCode);
        query.groupBy(languagePath);
        Expression<Long> count = builder.countDistinct(userJoin.get(UserEntity_.userId));
        query.select(builder.construct(statisticClass, languagePath, count));
        query.orderBy(builder.asc(languagePath));

        return entityManager.createQuery(query).getResultList();
    }
}
