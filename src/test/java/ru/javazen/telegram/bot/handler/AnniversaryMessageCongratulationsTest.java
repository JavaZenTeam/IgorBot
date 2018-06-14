package ru.javazen.telegram.bot.handler;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.telegram.telegrambots.api.methods.BotApiMethod;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Chat;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import ru.javazen.telegram.bot.AbsSenderStub;

import java.util.Arrays;
import java.util.Random;

import static org.junit.Assert.assertEquals;
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
        Chat chat = mock(Chat.class);

        when(chat.getId()).thenReturn(-7777777777777L);
        when(message.getChat()).thenReturn(chat);
        when(update.getMessage()).thenReturn(message);

        congratulations = new AnniversaryMessageCongratulations();
        congratulations.setRandom(new Random());
        congratulations.setTemplates(Arrays.asList("{0}", "{0}"));
        congratulations.setMessageIdPattern("\\d0{3,}");
    }

    @Test
    public void testMatched() throws Exception {
        when(update.getMessage().getMessageId()).thenReturn((1000));
        congratulations.handle(update, botMethodExecutor);
        BotApiMethod apiMethod = botMethodExecutor.getApiMethod();
        Assert.assertTrue(apiMethod instanceof SendMessage);
        SendMessage sendMessage = (SendMessage) apiMethod;
        assertEquals(update.getMessage().getChat().getId().toString(), sendMessage.getChatId());
        assertEquals(update.getMessage().getMessageId(), sendMessage.getReplyToMessageId());
        assertEquals(update.getMessage().getMessageId().toString(), sendMessage.getText());
    }

    @Test
    public void testNotMatched() throws Exception {
        when(update.getMessage().getMessageId()).thenReturn(6666);
        congratulations.handle(update, botMethodExecutor);
        BotApiMethod apiMethod = botMethodExecutor.getApiMethod();
        Assert.assertNull(apiMethod);
    }
}