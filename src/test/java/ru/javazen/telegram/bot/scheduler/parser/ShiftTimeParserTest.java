package ru.javazen.telegram.bot.scheduler.parser;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;

import java.util.Date;

public class ShiftTimeParserTest {

    private static ShiftTimeParser parser;
    private static Update update;


    @BeforeClass
    public static void initClass() {

        parser = new ShiftTimeParser(() -> "ok", "и+го+рь,\\s?ск[ао]ж[иы] через( .+)");

        update = Mockito.mock(Update.class);
        Message message = Mockito.mock(Message.class);
        Mockito.when(message.getReplyToMessage()).thenReturn(null);
        Mockito.when(update.getMessage()).thenReturn(message);
    }

    @Test
    public void canParse() {
        Assert.assertTrue(parser.canParse("Игорь, скажи через 2 часа привет"));
    }

    @Test
    public void cantParse() {
        Assert.assertFalse(parser.canParse("Игорь, скажи a"));
    }

    @Test
    public void checkDefaultMessage() {
        ScheduledMessageParser.ParseResult result = parser.parse("Игорь, скажи через 2 часа", update);

        Assert.assertEquals("ok", result.getMessage());
    }

    @Test
    public void checkUserMessage() {
        ScheduledMessageParser.ParseResult result = parser.parse("Игорь, скажи через 2 часа привет", update);

        Assert.assertEquals("привет", result.getMessage());
    }

    @Test
    public void checkTime() {
        ScheduledMessageParser.ParseResult result = parser.parse("Игорь, скажи через 2 часа привет", update);
        Date now = new Date();
        int twoHours = 1000 * 60 * 120;

        long diff = Math.abs((now.getTime() + twoHours) - result.getDate().toEpochMilli());

        Assert.assertTrue(diff < 1000);
    }
}
