package ru.javazen.telegram.bot.handler;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.javazen.telegram.bot.BotMethodExecutorStub;
import ru.javazen.telegram.bot.entity.Chat;
import ru.javazen.telegram.bot.entity.Message;
import ru.javazen.telegram.bot.entity.Update;
import ru.javazen.telegram.bot.method.ApiMethod;
import ru.javazen.telegram.bot.method.send.SendMessage;

import java.util.Arrays;
import java.util.Random;

import static org.junit.Assert.assertEquals;

public class AnniversaryMessageCongratulationsTest {
    private BotMethodExecutorStub botMethodExecutor;
    private Update update;
    private AnniversaryMessageCongratulations congratulations;

    @Before
    public void setUpStatic() throws Exception {
        botMethodExecutor = new BotMethodExecutorStub();

        update = new Update();
        Message message = new Message();
        Chat chat = new Chat();
        chat.setId(-7777777777777L);
        message.setChat(chat);
        update.setMessage(message);

        congratulations = new AnniversaryMessageCongratulations();
        congratulations.setRandom(new Random());
        congratulations.setTemplates(Arrays.asList("{0}", "{0}"));
        congratulations.setMessageIdPattern("\\d0{3,}");
    }

    @Test
    public void testMatched() throws Exception {
        update.getMessage().setMessageId(1000);
        congratulations.handle(update, botMethodExecutor);
        ApiMethod apiMethod = botMethodExecutor.getApiMethod();
        Assert.assertTrue(apiMethod instanceof SendMessage);
        SendMessage sendMessage = (SendMessage) apiMethod;
        assertEquals(update.getMessage().getChat().getId().toString(), sendMessage.getChatId());
        assertEquals(update.getMessage().getMessageId(), sendMessage.getReplyToMessageId());
        assertEquals(update.getMessage().getMessageId().toString(), sendMessage.getText());
    }

    @Test
    public void testNotMatched() throws Exception {
        update.getMessage().setMessageId(6666);
        congratulations.handle(update, botMethodExecutor);
        ApiMethod apiMethod = botMethodExecutor.getApiMethod();
        Assert.assertNull(apiMethod);
    }
}