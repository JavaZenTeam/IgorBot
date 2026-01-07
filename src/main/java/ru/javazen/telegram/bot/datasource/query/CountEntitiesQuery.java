package ru.javazen.telegram.bot.datasource.query;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.javazen.telegram.bot.datasource.model.EntityTypesCount;
import ru.javazen.telegram.bot.model.MessageEntity;
import ru.javazen.telegram.bot.model.MessageEntity_;
import ru.javazen.telegram.bot.util.DateRange;

import jakarta.persistence.EntityManager;

@Component
@RequiredArgsConstructor
public class CountEntitiesQuery {
    private final EntityManager entityManager;

    public EntityTypesCount totalCount(DateRange dateRange) {
        var builder = entityManager.getCriteriaBuilder();
        var query = builder.createQuery(EntityTypesCount.class);

        var messages = query.from(MessageEntity.class);

        query.select(builder.construct(EntityTypesCount.class,
                builder.countDistinct(messages.get(MessageEntity_.chat)),
                builder.countDistinct(messages.get(MessageEntity_.user))
        ));

        query.where(builder.lessThanOrEqualTo(messages.get(MessageEntity_.date), dateRange.getTo()));

        return entityManager.createQuery(query).getSingleResult();
    }

    public EntityTypesCount activeCount(DateRange dateRange) {
        var builder = entityManager.getCriteriaBuilder();
        var query = builder.createQuery(EntityTypesCount.class);

        var messages = query.from(MessageEntity.class);

        query.select(builder.construct(EntityTypesCount.class,
                builder.countDistinct(messages.get(MessageEntity_.chat)),
                builder.countDistinct(messages.get(MessageEntity_.user))
        ));

        query.where(builder.between(messages.get(MessageEntity_.date), dateRange.getFrom(), dateRange.getTo()));

        return entityManager.createQuery(query).getSingleResult();
    }
}
