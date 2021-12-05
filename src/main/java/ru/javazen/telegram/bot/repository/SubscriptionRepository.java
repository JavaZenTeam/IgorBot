package ru.javazen.telegram.bot.repository;

import org.springframework.data.repository.CrudRepository;
import ru.javazen.telegram.bot.model.MessagePK;
import ru.javazen.telegram.bot.model.Subscription;

import java.util.List;

public interface SubscriptionRepository extends CrudRepository<Subscription, MessagePK> {
    List<Subscription> findAllBySubscriptionPK_ChatId(Long chatId);

    Long countAllBySubscriptionPK_ChatIdAndUserId(Long chatId, Long userId);
}
