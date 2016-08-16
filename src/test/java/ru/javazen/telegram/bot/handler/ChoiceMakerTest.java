package ru.javazen.telegram.bot.handler;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import ru.javazen.telegram.bot.comparator.RandomComparator;

import java.util.*;

@RunWith(Parameterized.class)
public class ChoiceMakerTest {

    private Comparator<String> comparator = new RandomComparator(new Random());
    private ChoiceMaker chooser;
    private List<String> options;

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                { Arrays.asList("alpha", "beta") },
                { Arrays.asList("beta", "alpha") },
                { Arrays.asList("a", "b", "c") },
                { Arrays.asList("a", "c", "b") },
                { Arrays.asList("b", "a", "c") },
                { Arrays.asList("b", "c", "a") },
                { Arrays.asList("c", "b", "a") },
                { Arrays.asList("c", "a", "b") },
                { Arrays.asList("1", "2", "3", "4", "5") },
        });
    }

    @Before
    public void setUp() throws Exception {
        chooser = new ChoiceMaker();
        chooser.setPattern("\\s*(?<options>.+\\s+or\\s+.+)");
        chooser.setComparator(comparator);
        chooser.setSplitPattern("\\s*(,|or)\\s*");
    }

    public ChoiceMakerTest(List<String> options) {
        this.options = options;
    }

    @Test
    public void testHandle() throws Exception {
        String input = String.join(", ", options.subList(0, options.size() - 1)) + " or " + options.get(options.size() - 1);
        System.out.println("input: " + input);

        String expected = Collections.max(options, comparator);
        System.out.println("expected: " + expected);

        String actual = chooser.processText(input);
        System.out.println("actual: " + actual);

        Assert.assertEquals(expected, actual);
    }
}