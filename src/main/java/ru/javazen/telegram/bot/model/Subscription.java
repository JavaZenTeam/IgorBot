package ru.javazen.telegram.bot.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import ru.javazen.telegram.bot.util.StringMatchRule;
import ru.javazen.telegram.bot.util.WordSplitter;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Transient;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Entity
@Data
@EqualsAndHashCode(of = "subscriptionPK")
public class Subscription {
    @EmbeddedId
    private MessagePK subscriptionPK;

    private Integer userId;

    private String trigger;

    private String response;

    @Transient
    @Getter(lazy = true)
    private final List<String> words = WordSplitter.getInstance().apply(getTrigger());

    @Transient
    @Getter(lazy = true)
    private final List<Predicate<String>> rules = getWords().stream()
            .map(StringMatchRule::resolvePredicate)
            .collect(Collectors.toList());
}
