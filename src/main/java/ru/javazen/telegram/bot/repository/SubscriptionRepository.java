package ru.javazen.telegram.bot.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import ru.javazen.telegram.bot.model.Subscription;
import ru.javazen.telegram.bot.model.MessagePK;

import java.util.List;

public interface SubscriptionRepository extends CrudRepository<Subscription, MessagePK> {
    @Query("SELECT s FROM Subscription s " +
            "WHERE s.subscriptionPK.chatId = :chatId" +
            " AND lower(:trigger) LIKE lower(s.trigger)" +
            " AND (s.userId = :userId OR s.userId IS NULL)" +
            " ORDER BY s.subscriptionPK.messageId")
    List<Subscription> findAllByChatIdAndTriggerAndUserId(
            @Param("chatId") Long chatId,
            @Param("trigger") String trigger,
            @Param("userId") Integer userId);
}
