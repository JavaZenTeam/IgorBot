package ru.javazen.telegram.bot.datasource.query;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.javazen.telegram.bot.model.*;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.*;
import java.util.Date;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MessageCountQuery {
    private final EntityManager entityManager;

    public Long getChatMessageCount(Long chatId, Date date) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = builder.createQuery(Long.class);
        Root<MessageEntity> messages = query.from(MessageEntity.class);
        query.where(
                builder.equal(messages.get(MessageEntity_.chat), chatId),
                builder.lessThanOrEqualTo(messages.get(MessageEntity_.date), date)
        );


        ChatEntity chatEntity = entityManager.find(ChatEntity.class, chatId);


        Expression<Long> messageIdPath = messages.get(MessageEntity_.messagePK).get(MessagePK_.messageId).as(Long.class);
        Expression<Long> expression = chatEntity.getType() == ChatType.SUPERGROUP
                ? builder.max(messageIdPath)
                : builder.count(messageIdPath);

        query.select(expression);
        return Optional.ofNullable(entityManager.createQuery(query).getSingleResult())
                .orElse(0L);
    }

    public Long getUserMessageCount(Long userId, Date date) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = builder.createQuery(Long.class);
        Root<MessageEntity> messages = query.from(MessageEntity.class);
        query.where(
                builder.equal(messages.get(MessageEntity_.user), userId),
                builder.lessThanOrEqualTo(messages.get(MessageEntity_.date), date)
        );
        query.select(builder.count(messages.get(MessageEntity_.messagePK).get(MessagePK_.messageId)));
        return Optional.ofNullable(entityManager.createQuery(query).getSingleResult())
                .orElse(0L);
    }
}
