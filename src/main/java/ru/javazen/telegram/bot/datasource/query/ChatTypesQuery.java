package ru.javazen.telegram.bot.datasource.query;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.javazen.telegram.bot.datasource.model.BaseCount;
import ru.javazen.telegram.bot.model.*;
import ru.javazen.telegram.bot.util.DateRange;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ChatTypesQuery {

    private final EntityManager entityManager;

    public List<BaseCount<ChatType>> getChatTypes(DateRange dateRange) {
        Class<BaseCount<ChatType>> statisticClass = QueryUtils.countFor(ChatType.class);

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<BaseCount<ChatType>> query = builder.createQuery(statisticClass);

        Root<MessageEntity> messages = query.from(MessageEntity.class);
        Join<MessageEntity, ChatEntity> chatJoin = messages.join(MessageEntity_.chat);

        query.where(builder.between(messages.get(MessageEntity_.date), dateRange.getFrom(), dateRange.getTo()));
        Path<ChatType> chatTypePath = chatJoin.get(ChatEntity_.type);
        query.groupBy(chatTypePath);
        Expression<Long> count = builder.countDistinct(chatJoin.get(ChatEntity_.chatId));
        query.select(builder.construct(statisticClass, chatTypePath, count));
        query.orderBy(builder.asc(chatTypePath));

        return entityManager.createQuery(query).getResultList();
    }
}
