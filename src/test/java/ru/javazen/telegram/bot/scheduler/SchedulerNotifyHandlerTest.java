package ru.javazen.telegram.bot.scheduler;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.telegram.telegrambots.api.objects.Chat;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ru.javazen.telegram.bot.scheduler.parser.ScheduledMessageParser;
import ru.javazen.telegram.bot.scheduler.parser.TimeParser;
import ru.javazen.telegram.bot.scheduler.service.MessageSchedulerService;
import ru.javazen.telegram.bot.service.ChatConfigService;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import static org.mockito.Mockito.*;

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

    private Message message;

    @Before
    public void init() {
        handler = new SchedulerNotifyHandler(
                messageSchedulerService,
                DAYS_LIMIT,
                () -> "ok",
                Collections.singletonList(parser),
                chatConfigService);

        message = mock(Message.class);

        User user = mock(User.class);
        when(user.getId()).thenReturn(111);
        when(message.getFrom()).thenReturn(user);

        when(message.getChatId()).thenReturn(222L);

    }

    @Test
    public void testNullMessage() throws TelegramApiException {
        when(message.getText()).thenReturn(null);
        Assert.assertFalse(handler.handle(message, sender));
    }

//    @Test
//    public void testCorrectMessage() throws TelegramApiException {
//        String correctMessage = "Correct Message";
//        when(message.getText()).thenReturn(correctMessage);
//
//        when(parser.canParse(correctMessage)).thenReturn(true);
//        ScheduledMessageParser.ParseResult result = new ScheduledMessageParser.ParseResult(new Date(), "test", 1l);
//        when(parser.parse(correctMessage, message)).thenReturn(result);
//
//        Assert.assertTrue(handler.handle(message, sender));
//        verify(messageSchedulerService, Mockito.times(1)).scheduleTask(Mockito.any());
//    }

//    @Test
//    public void testTooLongDate() throws TelegramApiException {
//        String correctMessage = "Correct Message";
//        when(message.getText()).thenReturn(correctMessage);
//
//        when(parser.canParse(correctMessage)).thenReturn(true);
//
//        Calendar calendar = Calendar.getInstance();
//        calendar.add(Calendar.DAY_OF_YEAR, DAYS_LIMIT + 1);
//
//        ScheduledMessageParser.ParseResult result =
//                new ScheduledMessageParser.ParseResult(calendar.getTime(), "test", 1l);
//        when(parser.parse(correctMessage, message)).thenReturn(result);
//
//        Assert.assertTrue(handler.handle(message, sender));
//        verify(messageSchedulerService, Mockito.times(0)).scheduleTask(Mockito.any());
//    }

    @Test
    public void testWrongMessage() throws TelegramApiException {
        String correctMessage = "Correct Message";
        when(message.getText()).thenReturn("Wrong Message");

        when(parser.canParse(Mockito.anyString())).thenReturn(false);

        Assert.assertFalse(handler.handle(message, sender));
        verify(messageSchedulerService, Mockito.times(0)).scheduleTask(Mockito.any());
    }

    @Test
    public void testWrongParsing() throws TelegramApiException {
        String message = "Message";
        when(this.message.getText()).thenReturn(message);
        when(parser.canParse(message)).thenReturn(true);

        Assert.assertFalse(handler.handle(this.message, sender));
        verify(messageSchedulerService, Mockito.times(0)).scheduleTask(Mockito.any());
    }

    @Test
    public void checkFilteringWithCanParse() throws TelegramApiException {
        String message = "Message";
        when(this.message.getText()).thenReturn(message);
        when(parser.canParse(message)).thenReturn(true);

        handler.handle(this.message, sender);

        Mockito.verify(parser, Mockito.times(1)).parse(Mockito.anyString(), Mockito.any());
    }

    @Test
    public void checkFilteringWithCanNotParse() throws TelegramApiException {
        String message = "Message";
        when(this.message.getText()).thenReturn(message);
        when(parser.canParse(message)).thenReturn(false);

        handler.handle(this.message, sender);

        Mockito.verify(parser, Mockito.times(0)).parse(Mockito.anyString(), Mockito.any());
    }
}