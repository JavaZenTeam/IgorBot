package ru.javazen.telegram.bot;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@RunWith(Parameterized.class)
public class SplitWordTest {
    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {"Ё Й", Arrays.asList("Ё", "Й")},
                {"AA   C", Arrays.asList("AA", "C")},
                {"AA\nC", Arrays.asList("AA", "C")},
                {"AA 1 C", Arrays.asList("AA", "C")},
                {"AA, C", Arrays.asList("AA", "C")},
                {"A-A C", Arrays.asList("A-A", "C")},
                {"A C?", Arrays.asList("A", "C")},
                {"(A C", Arrays.asList("A", "C")},
                {"https://www.youtube.com/watch?v=IY5mBERhSDg", Collections.emptyList()},
        });
    }

    private String input;
    private List<String> expected;

    public SplitWordTest(String input, List<String> expected) {
        this.input = input;
        this.expected = expected;
    }

    @Test
    public void testSplitAsStream() {
        System.out.println(input);
        Pattern splitPattern = Pattern.compile("\\P{L}*(^|\\s+|$)\\P{L}*", Pattern.UNICODE_CHARACTER_CLASS);
        Pattern filterPattern = Pattern.compile("^[-\\p{L}]+$", Pattern.UNICODE_CHARACTER_CLASS);
        List<String> actual = splitPattern.splitAsStream(input)
                .filter(filterPattern.asPredicate())
                .collect(Collectors.toList());
        System.out.println(actual);

        Assert.assertArrayEquals(expected.toArray(), actual.toArray());
    }
}
