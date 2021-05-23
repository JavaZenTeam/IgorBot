package ru.javazen.telegram.bot.datasource.query;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.javazen.telegram.bot.model.MessageEntity;
import ru.javazen.telegram.bot.model.MessageEntity_;
import ru.javazen.telegram.bot.model.MessagePK_;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.Date;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MessageCountQuery {
    private final EntityManager entityManager;

    public Integer getChatMessageCount(Long chatId, Date date) {
        //todo check chat type somehow because max(messageId) works good in super groups only
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Integer> query = builder.createQuery(Integer.class);
        Root<MessageEntity> messages = query.from(MessageEntity.class);
        query.where(
                builder.equal(messages.get(MessageEntity_.chat), chatId),
                builder.lessThanOrEqualTo(messages.get(MessageEntity_.date), date)
        );
        query.select(builder.max(messages.get(MessageEntity_.messagePK).get(MessagePK_.messageId)));
        return Optional.ofNullable(entityManager.createQuery(query).getSingleResult()).orElse(0);
    }

    public Integer getUserMessageCount(Long userId, Date date) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Integer> query = builder.createQuery(Integer.class);
        Root<MessageEntity> messages = query.from(MessageEntity.class);
        query.where(
                builder.equal(messages.get(MessageEntity_.user), userId),
                builder.lessThanOrEqualTo(messages.get(MessageEntity_.date), date)
        );
        query.select(builder.max(messages.get(MessageEntity_.messagePK).get(MessagePK_.messageId)));
        return Optional.ofNullable(entityManager.createQuery(query).getSingleResult()).orElse(0);
    }
}
