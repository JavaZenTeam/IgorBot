package ru.javazen.telegram.bot.handler;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class RepeaterAdvancedTest {
    private static RepeaterAdvanced repeater;

    private String inputText;
    private String expectedAnswer;

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                { "say hello", "hello" },
                { "say hello world", "hello world" },
                { "111", null },
                {"say ", null },
        });
    }

    @BeforeClass
    public static void setUp() throws Exception {
        repeater = new RepeaterAdvanced();
        repeater.setPattern("say (.+)");
    }

    public RepeaterAdvancedTest(String inputText, String expectedAnswer) {
        this.inputText = inputText;
        this.expectedAnswer = expectedAnswer;
    }

    @Test
    public void testSolveAnswer() throws Exception {
        String actualAnswer = repeater.solveAnswer(inputText);
        org.junit.Assert.assertEquals(expectedAnswer, actualAnswer);
    }
}