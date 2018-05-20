package ru.javazen.telegram.bot.parser;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import ru.javazen.telegram.bot.scheduler.parser.ScheduledMessageParser;
import ru.javazen.telegram.bot.scheduler.parser.SpecificTimeParser;
import ru.javazen.telegram.bot.scheduler.parser.service.TimeExtractionService;
import ru.javazen.telegram.bot.scheduler.parser.service.TimeExtractionServiceImpl;
import ru.javazen.telegram.bot.scheduler.parser.service.unit.TimeUnitsMapper;

@RunWith(PowerMockRunner.class)
public class SpecificTimeParserTest {

    private static SpecificTimeParser parser;
    private static Update update;

    @BeforeClass
    public static void initClass() {
        TimeUnitsMapper mapper = new TimeUnitsMapper();
        TimeExtractionService service = new TimeExtractionServiceImpl(mapper);

        parser = new SpecificTimeParser(() -> "ok", "и+го+рь,\\s?ск[ао]ж[иы]( .+)", service);

        update = Mockito.mock(Update.class);
        Message message = Mockito.mock(Message.class);
        Mockito.when(message.getReplyToMessage()).thenReturn(null);
        Mockito.when(update.getMessage()).thenReturn(message);
    }

    @Test
    public void canParse() {
        Assert.assertTrue(parser.canParse("Игорь, скажи в 02:00 привет"));
    }

    @Test
    public void cantParse() {
        Assert.assertFalse(parser.canParse("Игорь, привет"));
    }

    @Test
    public void parseTime() {

        ScheduledMessageParser.ParseResult result
                = parser.parse("Игорь, скажи в 03:00 привет", update);

        System.out.println(result.getDate());
    }
}
