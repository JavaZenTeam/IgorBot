package ru.javazen.telegram.bot.util;

import lombok.AllArgsConstructor;

import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.IntStream;

@AllArgsConstructor(staticName = "getInstance")
public class SequenceMatcher<T> implements BiPredicate<List<Predicate<T>>, List<T>> {
    @Override
    public boolean test(List<Predicate<T>> predicates, List<T> fullList) {
        if (fullList.size() < predicates.size()) {
            return false;
        }
        return IntStream.rangeClosed(0, fullList.size() - predicates.size())
                .mapToObj(i -> fullList.subList(i, i + predicates.size()))
                .anyMatch(subList -> IntStream.range(0, predicates.size())
                        .allMatch(i -> predicates.get(i).test(subList.get(i))));
    }
}
