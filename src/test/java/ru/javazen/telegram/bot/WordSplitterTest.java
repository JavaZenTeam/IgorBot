package ru.javazen.telegram.bot;

import lombok.AllArgsConstructor;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import ru.javazen.telegram.bot.util.WordSplitter;

import java.util.Collection;
import java.util.List;

@RunWith(Parameterized.class)
@AllArgsConstructor
public class WordSplitterTest {
    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return List.of(new Object[][]{
                {"Ё Й", List.of("Ё", "Й")},
                {"AA   C", List.of("AA", "C")},
                {"AA\nC", List.of("AA", "C")},
                {"AA 1 C", List.of("AA", "1", "C")},
                {"AA, C", List.of("AA", "C")},
                {"A-A C", List.of("A-A", "C")},
                {"A C?", List.of("A", "C")},
                {"*A C%", List.of("*A", "C%")},
                {"(A C", List.of("A", "C")},
                {"https://www.youtube.com/watch?v=IY5mBERhSDg", List.of("https://www.youtube.com/watch?v=IY5mBERhSDg")},
        });
    }

    private final String input;
    private final List<String> expected;

    @Test
    public void testSplitAsStream() {
        List<String> actual = WordSplitter.getInstance().apply(input);
        Assert.assertArrayEquals(expected.toArray(), actual.toArray());
    }
}
