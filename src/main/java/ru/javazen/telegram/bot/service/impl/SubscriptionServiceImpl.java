package ru.javazen.telegram.bot.service.impl;

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
    private Map<Long, Map<Integer, Integer>> chatReplies = new HashMap<>();

    @Autowired
    public SubscriptionServiceImpl(SubscriptionRepository repository) {
        this.repository = repository;
    }

    @Override
    public Subscription createSubscription(Subscription template) {
        Subscription subscription = new Subscription();
        BeanUtils.copyProperties(template, subscription);
        validate(subscription);
        return repository.save(subscription);
    }

    private void validate(Subscription subscription) {
        Long countDuplicates = repository.countAllBySubscriptionPK_ChatIdAndUserIdAndTrigger(
                subscription.getSubscriptionPK().getChatId(),
                subscription.getUserId(),
                subscription.getTrigger());
        if (countDuplicates >= 3) {
            throw new TooManyDuplicatesException();
        }
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
        Map<Integer, Integer> replies = chatReplies.computeIfAbsent(subscriptionPK.getChatId(), (key) -> new HashMap<>());
        replies.put(replyMessageId, subscriptionPK.getMessageId());
    }

    @Override
    public boolean cancelSubscriptionByPK(MessagePK subscriptionPK) {
        Subscription subscription = repository.findById(subscriptionPK).orElse(null);
        if (subscription == null) return false;
        repository.delete(subscription);
        return true;
    }

    @Override
    public boolean cancelSubscriptionByReply(MessagePK replyMessagePK) {
        Map<Integer, Integer> replies = chatReplies.get(replyMessagePK.getChatId());
        if (replies == null) return false;
        Integer messageId = replies.remove(replyMessagePK.getMessageId());
        return messageId != null &&
                cancelSubscriptionByPK(new MessagePK(replyMessagePK.getChatId(), messageId));
    }
}
