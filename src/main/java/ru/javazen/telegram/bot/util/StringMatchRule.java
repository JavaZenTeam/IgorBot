package ru.javazen.telegram.bot.util;

import lombok.AllArgsConstructor;

import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

@AllArgsConstructor
public enum StringMatchRule implements BiPredicate<String, String> {
    EQUAL(String::equals, Function.identity()),
    START_WITH(String::startsWith, StringUtils::cutLast),
    END_WITH(String::endsWith, StringUtils::cutFirst),
    CONTAIN(String::contains, StringUtils::cutFirstLast),
    ;

    private static final String WILDCARD = "%";

    private final BiPredicate<String, String> rule;
    private final Function<String, String> preparation;

    @Override
    public boolean test(String pattern, String word) {
        return rule.test(word.toLowerCase(), preparation.apply(pattern).toLowerCase());
    }

    public static Predicate<String> resolvePredicate(String pattern) {
        return BiPredicateAdapter.of(pattern, resolve(pattern));
    }

    public static StringMatchRule resolve(String pattern) {
        if (pattern.endsWith(WILDCARD)) {
            if (pattern.startsWith(WILDCARD)) {
                return CONTAIN;
            } else {
                return START_WITH;
            }
        } else {
            if (pattern.startsWith(WILDCARD)) {
                return END_WITH;
            } else {
                return EQUAL;
            }
        }
    }
}
