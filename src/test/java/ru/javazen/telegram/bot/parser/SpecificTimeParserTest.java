package ru.javazen.telegram.bot.parser;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import ru.javazen.telegram.bot.scheduler.parser.ScheduledMessageParser;
import ru.javazen.telegram.bot.scheduler.parser.SpecificTimeParser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

@RunWith(PowerMockRunner.class)
public class SpecificTimeParserTest {

    private static SpecificTimeParser parser;
    private static Update update;

    private DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");

    @BeforeClass
    public static void initClass() {

        parser = new SpecificTimeParser(() -> "ok", "и+го+рь,\\s?ск[ао]ж[иы]( .+)");

        update = Mockito.mock(Update.class);
        Message message = Mockito.mock(Message.class);
        Mockito.when(message.getReplyToMessage()).thenReturn(null);
        Mockito.when(update.getMessage()).thenReturn(message);
    }

    @Test
    public void canParseTime() {
        Assert.assertTrue(parser.canParse("Игорь, скажи в 02:00 привет"));
    }

    @Test
    public void canParseDate() {
        Assert.assertTrue(parser.canParse("Игорь, скажи 23.06.2018 привет"));
    }

    @Test
    public void canParseDateTime() {
        Assert.assertTrue(parser.canParse("Игорь, скажи 23.06.2018 в 02:00 привет"));
    }

    @Test
    public void cantParse() {
        Assert.assertFalse(parser.canParse("Игорь, привет"));
    }

    @Ignore
    @Test
    public void parseTime() {

        ScheduledMessageParser.ParseResult result
                = parser.parse("Игорь, скажи в 03:00 привет", update);

        Assert.assertTrue("Expected: 03:00 in " + dateFormat.format(result.getDate()),
                dateFormat.format(result.getDate()).contains("03:00"));
    }

    @Ignore
    @Test
    public void parseDate() {

        ScheduledMessageParser.ParseResult result
                = parser.parse("Игорь, скажи 23.06.2050 привет", update);

        Assert.assertTrue("Expected: 23.06.2050 in " + dateFormat.format(result.getDate()),
                dateFormat.format(result.getDate()).contains("23.06.2050"));
    }

    @Ignore
    @Test
    public void parseDateTime() {

        ScheduledMessageParser.ParseResult result
                = parser.parse("Игорь, скажи 23.06.2050 в 03:00 привет", update);

        Assert.assertEquals(dateFormat.format(result.getDate()), "23.06.2050 03:00");
    }
}