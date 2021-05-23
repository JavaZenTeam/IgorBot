package ru.javazen.telegram.bot.datasource.query;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.javazen.telegram.bot.datasource.model.Statistic;
import ru.javazen.telegram.bot.model.FileType;
import ru.javazen.telegram.bot.model.MessageEntity;
import ru.javazen.telegram.bot.model.MessageEntity_;
import ru.javazen.telegram.bot.util.DateRange;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import javax.persistence.metamodel.SingularAttribute;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TopUsedStickerQuery {
    private final EntityManager entityManager;

    @Transactional(readOnly = true)
    public List<Statistic<String>> getTopUsedChatStickers(Long chatId, DateRange dateRange, Integer maxResults) {
        return getTopUsedStickers(chatId, dateRange, maxResults, MessageEntity_.chat);
    }

    @Transactional(readOnly = true)
    public List<Statistic<String>> getTopUsedUserStickers(Long chatId, DateRange dateRange, Integer maxResults) {
        return getTopUsedStickers(chatId, dateRange, maxResults, MessageEntity_.user);
    }

    private List<Statistic<String>> getTopUsedStickers(Long objectId, DateRange dateRange, Integer maxResults,
                                                       SingularAttribute<MessageEntity, ?> objectAttr) {
        Class<Statistic<String>> statisticClass = QueryUtils.statisticClassFor(String.class);

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Statistic<String>> query = builder.createQuery(statisticClass);

        Root<MessageEntity> messages = query.from(MessageEntity.class);
        query.where(
                builder.equal(messages.get(MessageEntity_.fileType), FileType.STICKER),
                builder.equal(messages.get(objectAttr), objectId),
                builder.between(messages.get(MessageEntity_.date), dateRange.getFrom(), dateRange.getTo()));
        Path<String> fileUniqueId = messages.get(MessageEntity_.fileUniqueId);
        query.groupBy(fileUniqueId);

        Expression<String> fileId = builder.greatest(messages.get(MessageEntity_.fileId));
        Expression<Long> count = builder.count(messages);
        query.select(builder.construct(statisticClass, fileId, count));
        query.orderBy(builder.desc(count));

        return entityManager.createQuery(query)
                .setMaxResults(Optional.ofNullable(maxResults).orElse(5))
                .getResultList();
    }
}
