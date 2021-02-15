package ru.javazen.telegram.bot.util;

import lombok.AllArgsConstructor;

import java.util.function.BiPredicate;
import java.util.function.Predicate;

@AllArgsConstructor(staticName = "of")
public class BiPredicateAdapter<T, U> implements Predicate<U> {
    private final T t;
    private final BiPredicate<T, U> delegate;

    @Override
    public boolean test(U u) {
        return delegate.test(t, u);
    }
}
