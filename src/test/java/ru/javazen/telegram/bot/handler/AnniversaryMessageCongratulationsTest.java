package ru.javazen.telegram.bot.handler;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.javazen.telegram.bot.AbsSenderStub;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AnniversaryMessageCongratulationsTest {
    private AbsSenderStub botMethodExecutor;
    private Update update;
    private AnniversaryMessageCongratulations congratulations;

    @Before
    public void setUpStatic() throws Exception {
        botMethodExecutor = new AbsSenderStub();

        update = mock(Update.class);
        Message message = mock(Message.class);

        when(message.getChatId()).thenReturn(-7777777777777L);
        when(update.getMessage()).thenReturn(message);
        when(update.hasMessage()).thenReturn(true);

        congratulations = new AnniversaryMessageCongratulations();
        congratulations.setTemplateSupplier(() -> "EEE!");
        congratulations.setMessageIdPattern("\\d0{2,}");
    }

    @Test
    public void testMatched() throws Exception {
        when(update.getMessage().getMessageId()).thenReturn((1000));
        assertFalse(congratulations.handle(update, botMethodExecutor));
        BotApiMethod apiMethod = botMethodExecutor.getApiMethod();
        assertTrue(apiMethod instanceof SendMessage);
        SendMessage sendMessage = (SendMessage) apiMethod;
        assertEquals(update.getMessage().getChatId().toString(), sendMessage.getChatId());
        assertEquals(update.getMessage().getMessageId(), sendMessage.getReplyToMessageId());
        assertEquals("EEE!", sendMessage.getText());
    }

    @Test
    public void testNotMatched() throws Exception {
        when(update.getMessage().getMessageId()).thenReturn(6666);
        assertFalse(congratulations.handle(update, botMethodExecutor));
        BotApiMethod apiMethod = botMethodExecutor.getApiMethod();
        Assert.assertNull(apiMethod);
    }
}