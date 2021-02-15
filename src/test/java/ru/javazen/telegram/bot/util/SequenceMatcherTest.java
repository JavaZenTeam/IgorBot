package ru.javazen.telegram.bot.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.Predicate;

class SequenceMatcherTest {
    private final SequenceMatcher<String> matcher = SequenceMatcher.getInstance();

    @Test
    void testSingle() {
        List<Predicate<String>> predicates = List.of("B"::equals);
        List<String> words = List.of("A", "B", "C");
        Assertions.assertTrue(matcher.test(predicates, words));
    }

    @Test
    void testFull() {
        List<Predicate<String>> predicates = List.of("A"::equals, "B"::equals, "C"::equals);
        List<String> words = List.of("A", "B", "C");
        Assertions.assertTrue(matcher.test(predicates, words));
    }

    @Test
    void testStart() {
        List<Predicate<String>> predicates = List.of("A"::equals, "B"::equals);
        List<String> words = List.of("A", "B", "C", "D");
        Assertions.assertTrue(matcher.test(predicates, words));
    }

    @Test
    void testEnd() {
        List<Predicate<String>> predicates = List.of("C"::equals, "D"::equals);
        List<String> words = List.of("A", "B", "C", "D");
        Assertions.assertTrue(matcher.test(predicates, words));
    }

    @Test
    void testMiddle() {
        List<Predicate<String>> predicates = List.of("B"::equals, "C"::equals);
        List<String> words = List.of("A", "B", "C", "D");
        Assertions.assertTrue(matcher.test(predicates, words));
    }

    @Test
    void testNone() {
        List<Predicate<String>> predicates = List.of("A"::equals, "B"::equals, "C"::equals);
        List<String> words = List.of("A", "B", "A", "C");
        Assertions.assertFalse(matcher.test(predicates, words));
    }
}