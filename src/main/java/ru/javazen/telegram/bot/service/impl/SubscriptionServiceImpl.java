package ru.javazen.telegram.bot.service.impl;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.javazen.telegram.bot.model.MessagePK;
import ru.javazen.telegram.bot.model.Subscription;
import ru.javazen.telegram.bot.repository.SubscriptionRepository;
import ru.javazen.telegram.bot.service.SubscriptionService;
import ru.javazen.telegram.bot.util.SequenceMatcher;
import ru.javazen.telegram.bot.util.StringMatchRule;
import ru.javazen.telegram.bot.util.StringUtils;
import ru.javazen.telegram.bot.util.WordSplitter;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Getter(AccessLevel.PROTECTED)
public class SubscriptionServiceImpl implements SubscriptionService {
    private static final String QUOTE = "\"";

    private final SubscriptionRepository repository;

    /**
     * key: Reply messagePK
     * value: origin subscriptionPK
     */
    private final Cache<MessagePK, MessagePK> replies = CacheBuilder.newBuilder()
            .expireAfterWrite(Duration.ofHours(6))
            .build();

    /**
     * key: chatId
     * value: all Subscriptions in the chat
     */
    private final LoadingCache<Long, List<Subscription>> subscriptions = CacheBuilder.newBuilder()
            .expireAfterAccess(Duration.ofHours(2))
            .build(CacheLoader.from((key) -> getRepository().findAllBySubscriptionPK_ChatId(key)));

    @Override
    public void createSubscription(Subscription template) {
        getSubscriptions().invalidate(template.getSubscriptionPK().getChatId());
        validate(template);
        repository.save(template);
    }

    private void validate(Subscription subscription) {
        Long countDuplicates = repository.countAllBySubscriptionPK_ChatIdAndUserId(
                subscription.getSubscriptionPK().getChatId(),
                subscription.getUserId());
        if (countDuplicates >= 100) {
            throw new TooManyDuplicatesException();
        }
    }

    @Override
    public List<Subscription> catchSubscriptions(Long chatId, Integer userId, String text) {
        List<String> words = WordSplitter.getInstance().apply(text);
        List<Subscription> subscriptions = getSubscriptions().getUnchecked(chatId);
        return subscriptions.stream()
                .filter(s -> s.getUserId() == null || s.getUserId().equals(userId))
                .filter(s -> s.getTrigger().startsWith(QUOTE) && s.getTrigger().endsWith(QUOTE)
                        ? StringMatchRule.resolvePredicate(StringUtils.cutFirstLast(s.getTrigger())).test(text)
                        : SequenceMatcher.<String>getInstance().test(s.getRules(), words))
                .collect(Collectors.toList());
    }

    @Override
    public void saveSubscriptionReply(MessagePK subscriptionPK, int replyMessageId) {
        getReplies().put(new MessagePK(subscriptionPK.getChatId(), replyMessageId), subscriptionPK);
    }

    @Override
    public boolean cancelSubscriptionByPK(MessagePK subscriptionPK) {
        return repository.findById(subscriptionPK)
                .map(subscription -> {
                    repository.delete(subscription);
                    getSubscriptions().invalidate(subscriptionPK.getChatId());
                    return true;
                })
                .orElse(false);
    }

    @Override
    public boolean cancelSubscriptionByReply(MessagePK replyMessagePK) {
        return Optional.ofNullable(getReplies().getIfPresent(replyMessagePK))
                .map(this::cancelSubscriptionByPK)
                .orElse(false);
    }
}
