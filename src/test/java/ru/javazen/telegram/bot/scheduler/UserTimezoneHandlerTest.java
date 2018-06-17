package ru.javazen.telegram.bot.scheduler;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ru.javazen.telegram.bot.handler.UserTimezoneHandler;
import ru.javazen.telegram.bot.service.ChatConfigService;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserTimezoneHandlerTest {

    private static final int USER_ID = 111;
    private static final String TIMEZONE_OFFSET_CONFIG_KEY = "TIMEZONE_OFFSET";

    private UserTimezoneHandler userTimezoneHandler;

    @Mock
    private ChatConfigService chatConfigService;

    @Mock
    private AbsSender sender;

    private Update update;

    @Before
    public void init() {
        userTimezoneHandler = new UserTimezoneHandler(chatConfigService);

        update = mock(Update.class);
        Message message = mock(Message.class);
        when(update.getMessage()).thenReturn(message);
        when(update.hasMessage()).thenReturn(true);

        User user = mock(User.class);
        when(user.getId()).thenReturn(USER_ID);
        when(message.getFrom()).thenReturn(user);
    }

    @Test
    public void testFullParsePositive() throws TelegramApiException {
        when(update.getMessage().getText()).thenReturn("таймзона +04:00");
        Assert.assertTrue(userTimezoneHandler.handle(update, sender));

        verify(chatConfigService).setProperty(USER_ID, TIMEZONE_OFFSET_CONFIG_KEY, "+04:00");
    }

    @Test
    public void testFullParseNegative() throws TelegramApiException {
        when(update.getMessage().getText()).thenReturn("таймзона -17:30");
        Assert.assertTrue(userTimezoneHandler.handle(update, sender));

        verify(chatConfigService).setProperty(USER_ID, TIMEZONE_OFFSET_CONFIG_KEY, "-17:30");
    }

    @Test
    public void testParsePositive() throws TelegramApiException {
        when(update.getMessage().getText()).thenReturn("таймзона +4");
        Assert.assertTrue(userTimezoneHandler.handle(update, sender));

        verify(chatConfigService).setProperty(USER_ID, TIMEZONE_OFFSET_CONFIG_KEY, "+04:00");
    }

    @Test
    public void testParseNegative() throws TelegramApiException {
        when(update.getMessage().getText()).thenReturn("таймзона -1:2");
        Assert.assertTrue(userTimezoneHandler.handle(update, sender));

        verify(chatConfigService).setProperty(USER_ID, TIMEZONE_OFFSET_CONFIG_KEY, "-01:02");
    }

    @Test
    public void cantParse() throws TelegramApiException {
        when(update.getMessage().getText()).thenReturn("таймзона @#$");
        Assert.assertFalse(userTimezoneHandler.handle(update, sender));
    }

    @Test
    public void overHours() throws TelegramApiException {
        when(update.getMessage().getText()).thenReturn("таймзона -19:15");
        Assert.assertFalse(userTimezoneHandler.handle(update, sender));
    }

    @Test
    public void overMinutes() throws TelegramApiException {
        when(update.getMessage().getText()).thenReturn("таймзона 12:77");
        Assert.assertFalse(userTimezoneHandler.handle(update, sender));
    }

    @Test
    public void noMessage() throws TelegramApiException {
        when(update.hasMessage()).thenReturn(false);
        Assert.assertFalse(userTimezoneHandler.handle(update, sender));
    }
}