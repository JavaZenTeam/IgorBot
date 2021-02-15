package ru.javazen.telegram.bot.service;

import ru.javazen.telegram.bot.model.Subscription;
import ru.javazen.telegram.bot.model.MessagePK;

import java.util.List;

public interface SubscriptionService {
    void createSubscription(Subscription template);

    List<Subscription> catchSubscriptions(Long chatId, Integer userId, String text);

    void saveSubscriptionReply(MessagePK subscriptionPK, int replyMessageId);

    boolean cancelSubscriptionByPK(MessagePK subscriptionPK);

    boolean cancelSubscriptionByReply(MessagePK replyMessagePK);

    class TooManyDuplicatesException extends RuntimeException {
    }
}
