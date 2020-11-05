package ru.javazen.telegram.bot.scheduler.parser;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.javazen.telegram.bot.service.ChatConfigService;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

@RunWith(MockitoJUnitRunner.class)
public class SpecificTimeParserTest {

    private SpecificTimeParser parser;
    private Message message;

    private DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");

    @Mock
    private ChatConfigService chatConfigService;

    @Before
    public void init() {

        parser = new SpecificTimeParser(() -> "ok",
                "и+го+рь,\\s?ск[ао]ж[иы]( .+)",
                chatConfigService);

        message = Mockito.mock(Message.class);
        User user = Mockito.mock(User.class);
        Mockito.when(user.getId()).thenReturn(111);

        Mockito.when(message.getReplyToMessage()).thenReturn(null);
        Mockito.when(message.getFrom()).thenReturn(user);
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
                = parser.parse("Игорь, скажи в 03:00 привет", message);

        Assert.assertTrue("Expected: 03:00 in " + dateFormat.format(result.getDate()),
                dateFormat.format(result.getDate()).contains("03:00"));
    }

    @Ignore
    @Test
    public void parseDate() {

        ScheduledMessageParser.ParseResult result
                = parser.parse("Игорь, скажи 23.06.2050 привет", message);

        Assert.assertTrue("Expected: 23.06.2050 in " + dateFormat.format(result.getDate()),
                dateFormat.format(result.getDate()).contains("23.06.2050"));
    }

    @Ignore
    @Test
    public void parseDateTime() {

        ScheduledMessageParser.ParseResult result
                = parser.parse("Игорь, скажи 23.06.2050 в 03:00 привет", message);

        Assert.assertEquals(dateFormat.format(result.getDate()), "23.06.2050 03:00");
    }

    @Test
    public void correctParse() {
        ScheduledMessageParser.ParseResult result
                = parser.parse("Игорь, скажи в 21:00 привет", message);

        Assert.assertTrue(result != null && result.getDate() != null && result.getMessage() != null);
    }

    @Test
    public void wrongParse() {
        ScheduledMessageParser.ParseResult result
                = parser.parse("Игорь, скажи !!! привет", message);

        Assert.assertNull(result);
    }

    @Test
    public void checkDefaultMessage() {
        ScheduledMessageParser.ParseResult result = parser.parse("Игорь, скажи в 21:00", message);

        Assert.assertEquals("ok", result.getMessage());
    }

    @Test
    public void checkUserMessage() {
        ScheduledMessageParser.ParseResult result = parser.parse("Игорь, скажи в 21:00 привет", message);

        Assert.assertEquals("привет", result.getMessage());
    }
}