package ru.javazen.telegram.bot.service;

import ru.javazen.telegram.bot.model.Subscription;
import ru.javazen.telegram.bot.model.MessagePK;

import java.util.List;

public interface SubscriptionService {
    Subscription createSubscription(Subscription template);
    List<Subscription> catchSubscriptions(Subscription template);
    void saveSubscriptionReply(MessagePK subscriptionPK, int replyMessageId);
    boolean cancelSubscriptionByPK(MessagePK subscriptionPK);
    boolean cancelSubscriptionByReply(MessagePK replyMessagePK);
}
