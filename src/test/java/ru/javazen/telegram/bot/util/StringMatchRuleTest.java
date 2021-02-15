package ru.javazen.telegram.bot.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StringMatchRuleTest {
    @Test
    void testResolveEqual() {
        assertEquals(StringMatchRule.resolve("test"), StringMatchRule.EQUAL);
    }
    @Test
    void testResolveStartWith() {
        assertEquals(StringMatchRule.resolve("test%"), StringMatchRule.START_WITH);
    }
    @Test
    void testResolveEndWith() {
        assertEquals(StringMatchRule.resolve("%test"), StringMatchRule.END_WITH);
    }
    @Test
    void testResolveContain() {
        assertEquals(StringMatchRule.resolve("%test%"), StringMatchRule.CONTAIN);
    }


    @Test
    void testMatchEqual() {
        assertTrue(StringMatchRule.resolvePredicate("test").test("test"));
    }
    @Test
    void testMatchStartWith() {
        assertTrue(StringMatchRule.resolvePredicate("test%").test("test_end"));
    }
    @Test
    void testMatchEndWith() {
        assertTrue(StringMatchRule.resolvePredicate("%test").test("start_test"));
    }
    @Test
    void testMatchContain() {
        assertTrue(StringMatchRule.resolvePredicate("%test%").test("start_test_end"));
    }
}