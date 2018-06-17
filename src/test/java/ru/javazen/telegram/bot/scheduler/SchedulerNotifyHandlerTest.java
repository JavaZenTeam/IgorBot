package ru.javazen.telegram.bot.scheduler;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.telegram.telegrambots.api.objects.Chat;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ru.javazen.telegram.bot.scheduler.parser.ScheduledMessageParser;
import ru.javazen.telegram.bot.scheduler.service.MessageSchedulerService;
import ru.javazen.telegram.bot.service.ChatConfigService;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SchedulerNotifyHandlerTest {

    private static final int DAYS_LIMIT = 100;
    private SchedulerNotifyHandler handler;

    @Mock
    private MessageSchedulerService messageSchedulerService;

    @Mock
    private ScheduledMessageParser parser;

    @Mock
    private AbsSender sender;

    @Mock
    private ChatConfigService chatConfigService;

    private Update update;

    @Before
    public void init() {
        handler = new SchedulerNotifyHandler(
                messageSchedulerService,
                DAYS_LIMIT,
                () -> "ok",
                Collections.singletonList(parser),
                chatConfigService);

        update = mock(Update.class);
        Message message = mock(Message.class);
        when(update.getMessage()).thenReturn(message);

        User user = mock(User.class);
        when(user.getId()).thenReturn(111);
        when(message.getFrom()).thenReturn(user);

        Chat chat = mock(Chat.class);
        when(chat.getId()).thenReturn(222L);
        when(message.getChat()).thenReturn(chat);

    }

    @Test
    public void testNullMessage() throws TelegramApiException {
        when(update.getMessage().getText()).thenReturn(null);
        Assert.assertFalse(handler.handle(update, sender));;
    }

    @Test
    public void testCorrectMessage() throws TelegramApiException {
        String correctMessage = "Correct Message";
        when(update.getMessage().getText()).thenReturn(correctMessage);

        when(parser.canParse(correctMessage)).thenReturn(true);
        ScheduledMessageParser.ParseResult result = new ScheduledMessageParser.ParseResult(new Date(), "test");
        when(parser.parse(correctMessage, update)).thenReturn(result);

        Assert.assertTrue(handler.handle(update, sender));
        verify(messageSchedulerService, Mockito.times(1)).scheduleTask(Mockito.any());
    }

    @Test
    public void testTooLongDate() throws TelegramApiException {
        String correctMessage = "Correct Message";
        when(update.getMessage().getText()).thenReturn(correctMessage);

        when(parser.canParse(correctMessage)).thenReturn(true);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, DAYS_LIMIT + 1);

        ScheduledMessageParser.ParseResult result =
                new ScheduledMessageParser.ParseResult(calendar.getTime(), "test");
        when(parser.parse(correctMessage, update)).thenReturn(result);

        Assert.assertTrue(handler.handle(update, sender));
        verify(messageSchedulerService, Mockito.times(0)).scheduleTask(Mockito.any());
    }

    @Test
    public void testWrongMessage() throws TelegramApiException {
        String correctMessage = "Correct Message";
        when(update.getMessage().getText()).thenReturn("Wrong Message");

        when(parser.canParse(Mockito.anyString())).thenReturn(false);

        Assert.assertFalse(handler.handle(update, sender));
        verify(messageSchedulerService, Mockito.times(0)).scheduleTask(Mockito.any());
    }

    @Test
    public void testWrongParsing() throws TelegramApiException {
        String message = "Message";
        when(update.getMessage().getText()).thenReturn(message);
        when(parser.canParse(message)).thenReturn(true);

        Assert.assertFalse(handler.handle(update, sender));
        verify(messageSchedulerService, Mockito.times(0)).scheduleTask(Mockito.any());
    }

    @Test
    public void checkFilteringWithCanParse() throws TelegramApiException {
        String message = "Message";
        when(update.getMessage().getText()).thenReturn(message);
        when(parser.canParse(message)).thenReturn(true);

        handler.handle(update, sender);

        Mockito.verify(parser, Mockito.times(1)).parse(Mockito.anyString(), Mockito.anyObject());
    }

    @Test
    public void checkFilteringWithCanNotParse() throws TelegramApiException {
        String message = "Message";
        when(update.getMessage().getText()).thenReturn(message);
        when(parser.canParse(message)).thenReturn(false);

        handler.handle(update, sender);

        Mockito.verify(parser, Mockito.times(0)).parse(Mockito.anyString(), Mockito.anyObject());
    }
}