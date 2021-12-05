package ru.javazen.telegram.bot.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.javazen.telegram.bot.AbsSenderStub;

import java.io.IOException;
import java.util.Collections;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Ignore
public class UpdateInfoProviderTest {
    private static final String INVALID_PATH_MESSAGE = "invalid";
    private Update update;
    private AbsSenderStub botMethodExecutor;
    private ObjectMapper mapper;
    private UpdateInfoProvider updateInfoProvider;

    @Before
    public void setUpStatic() throws Exception {
        botMethodExecutor = new AbsSenderStub();
        mapper = new ObjectMapper();

        update = mock(Update.class);
        when(update.getUpdateId()).thenReturn(5555555);
        Message message = mock(Message.class);
        when(message.getMessageId()).thenReturn(777777);
        User from = mock(User.class);
        when(from.getId()).thenReturn(909090909L);
        when(from.getFirstName()).thenReturn("Mr. User");
        when(message.getFrom()).thenReturn(from);
        when(message.getDate()).thenReturn(1511723066);
        Chat chat = mock(Chat.class);
        when(chat.getId()).thenReturn(-7777777777777L);
        when(chat.isSuperGroupChat()).thenReturn(true);
        when(chat.getTitle()).thenReturn("My Chat");
        when(message.getChat()).thenReturn(chat);
        when(message.getText()).thenReturn("");
        MessageEntity messageEntity = mock(MessageEntity.class);
        when(messageEntity.getType()).thenReturn("bot_command");
        when(messageEntity.getOffset()).thenReturn(0);
        when(messageEntity.getLength()).thenReturn(5);
        when(message.getEntities()).thenReturn(Collections.singletonList(messageEntity));
        when(update.getMessage()).thenReturn(message);

        updateInfoProvider = new UpdateInfoProvider();
        updateInfoProvider.setMapper(mapper);
        updateInfoProvider.setInvalidPathMessage(INVALID_PATH_MESSAGE);
    }

    @Test
    public void handleSimple() throws Exception {
        testSuccess("/info", update);
    }

    @Test
    public void handleReply() throws Exception {
        Message replyToMessage = mock(Message.class);
        when(replyToMessage.getMessageId()).thenReturn(777770);
        when(replyToMessage.getFrom()).thenReturn(update.getMessage().getFrom());
        when(replyToMessage.getDate()).thenReturn(1511723066);
        when(replyToMessage.getChat()).thenReturn(update.getMessage().getChat());
        when(replyToMessage.getText()).thenReturn("replyToMessage text");
        when(update.getMessage().getReplyToMessage()).thenReturn(replyToMessage);

        testSuccess("/info", replyToMessage);
    }

    @Test
    public void handlePathEntity() throws Exception {
        testSuccess("/info message.chat", update.getMessage().getChat());
    }

    @Test
    public void handleReplyPath() throws Exception {
        Message replyToMessage = mock(Message.class);
        when(replyToMessage.getMessageId()).thenReturn(777770);
        Chat chat = update.getMessage().getChat();
        when(replyToMessage.getChat()).thenReturn(chat);
        when(replyToMessage.getText()).thenReturn("replyToMessage text");
        when(update.getMessage().getReplyToMessage()).thenReturn(replyToMessage);

        testSuccess("/info chat", replyToMessage.getChat());
    }

    @Test
    public void handlePathString() throws Exception {
        testSuccess("/info Message.from.FirstName", update.getMessage().getFrom().getFirstName());
    }

    @Test
    public void handlePathNumber() throws Exception {
        testSuccess("/info Message.MessageId", update.getMessage().getMessageId());
    }

    @Test
    public void handleNull() throws Exception {
        testSuccess("/info Message.from.LastName", update.getMessage().getFrom().getLastName());
    }

    @Test
    public void handleInvalidPath() throws Exception {
        testFailed("/info Message.from.GGGGGGG");
    }

    @Test
    public void handleNullPointer() throws Exception {
        testFailed("/info editedMessage.chat");
    }


    private void testFailed(String input) throws TelegramApiException {
        when(update.getMessage().getText()).thenReturn(input);
        updateInfoProvider.handle(update, botMethodExecutor);
        BotApiMethod apiMethod = botMethodExecutor.getApiMethod();
        Assert.assertTrue(apiMethod instanceof SendMessage);
        SendMessage sendMessage = (SendMessage) apiMethod;
        Assert.assertEquals(update.getMessage().getChat().getId().toString(), sendMessage.getChatId());
        Assert.assertEquals(INVALID_PATH_MESSAGE, sendMessage.getText());
    }

    private void testSuccess(String input, Object expected) throws IOException, TelegramApiException {
        when(update.getMessage().getText()).thenReturn(input);
        updateInfoProvider.handle(update, botMethodExecutor);
        BotApiMethod apiMethod = botMethodExecutor.getApiMethod();
        Assert.assertTrue(apiMethod instanceof SendMessage);
        SendMessage sendMessage = (SendMessage) apiMethod;
        Assert.assertEquals(update.getMessage().getChat().getId().toString(), sendMessage.getChatId());
        Assert.assertEquals(mapper.writeValueAsString(expected), sendMessage.getText().substring(4, sendMessage.getText().length() - 3));
    }
}