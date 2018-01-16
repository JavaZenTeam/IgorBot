package ru.javazen.telegram.bot.service.impl;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableBiMap;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.javazen.telegram.bot.model.MessagePK;
import ru.javazen.telegram.bot.model.Subscription;
import ru.javazen.telegram.bot.repository.SubscriptionRepository;
import ru.javazen.telegram.bot.service.SubscriptionService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SubscriptionServiceImpl implements SubscriptionService {
    private SubscriptionRepository repository;
    private Map<Long, BiMap<Integer, Integer>> chatReplies = new HashMap<>();

    @Autowired
    public SubscriptionServiceImpl(SubscriptionRepository repository) {
        this.repository = repository;
    }

    @Override
    public Subscription createSubscription(Subscription template) {
        Subscription subscription = new Subscription();
        BeanUtils.copyProperties(template, subscription);
        return repository.save(subscription);
    }

    @Override
    public List<Subscription> catchSubscriptions(Subscription template) {
        return repository.findAllByChatIdAndTriggerAndUserId(
                template.getSubscriptionPK().getChatId(),
                template.getTrigger(),
                template.getUserId());
    }

    @Override
    public void saveSubscriptionReply(MessagePK subscriptionPK, int replyMessageId) {
        BiMap<Integer, Integer> replies = chatReplies.computeIfAbsent(subscriptionPK.getChatId(), (key) -> HashBiMap.create());
        replies.forcePut(replyMessageId, subscriptionPK.getMessageId());
    }

    @Override
    public boolean cancelSubscriptionByPK(MessagePK subscriptionPK) {
        Subscription subscription = repository.findOne(subscriptionPK);
        if (subscription == null) return false;
        repository.delete(subscription);
        return true;
    }

    @Override
    public boolean cancelSubscriptionByReply(MessagePK replyMessagePK) {
        Integer messageId = chatReplies
                .getOrDefault(replyMessagePK.getChatId(), ImmutableBiMap.of())
                .remove(replyMessagePK.getMessageId());

        return messageId != null &&
                cancelSubscriptionByPK(new MessagePK(replyMessagePK.getChatId(), messageId));
    }
}
