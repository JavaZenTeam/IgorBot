package ru.javazen.telegram.bot.handler;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.javazen.telegram.bot.BotMethodExecutorStub;
import ru.javazen.telegram.bot.entity.*;
import ru.javazen.telegram.bot.method.ApiMethod;
import ru.javazen.telegram.bot.method.send.SendMessage;

import java.io.IOException;
import java.util.Collections;

public class UpdateInfoProviderTest {
    private static final String INVALID_PATH_MESSAGE = "invalid";
    private static Update update;
    private static BotMethodExecutorStub botMethodExecutor;
    private static ObjectMapper mapper;
    private UpdateInfoProvider updateInfoProvider;

    @BeforeClass
    public static void setUpStatic() throws Exception {
        botMethodExecutor = new BotMethodExecutorStub();
        mapper = new ObjectMapper();

        update = new Update();
        update.setUpdateId(5555555);
        Message message = new Message();
        message.setMessageId(777777);
        User from = new User();
        from.setId(909090909);
        from.setFirstName("Mr. User");
        message.setFrom(from);
        message.setDate(1511723066);
        Chat chat = new Chat();
        chat.setId(-7777777777777L);
        chat.setType(Chat.SUPERGROUP_CHAT_TYPE);
        chat.setTitle("My Chat");
        message.setChat(chat);
        Message replyToMessage = new Message();
        replyToMessage.setMessageId(777770);
        replyToMessage.setFrom(from);
        replyToMessage.setDate(1511723066);
        replyToMessage.setChat(chat);
        replyToMessage.setText("replyToMessage text");
        message.setReplyToMessage(replyToMessage);
        message.setText("");
        MessageEntity messageEntity = new MessageEntity();
        messageEntity.setType(MessageEntity.BOT_COMMAND_TYPE);
        messageEntity.setOffset(0);
        messageEntity.setLength(5);
        message.setEntities(Collections.singletonList(messageEntity));
        update.setMessage(message);
    }

    @Before
    public void setUp() throws Exception {
        botMethodExecutor.execute(null, null);
        updateInfoProvider = new UpdateInfoProvider();
        updateInfoProvider.setMapper(mapper);
        updateInfoProvider.setInvalidPathMessage(INVALID_PATH_MESSAGE);
        update.getMessage().setText("");
    }

    @Test
    public void handleSimple() throws Exception {
        testSuccess("/info", update);
    }

    @Test
    public void handlePathEntity() throws Exception {
        testSuccess("/info message.chat", update.getMessage().getChat());
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


    private void testFailed(String input){
        update.getMessage().setText(input);
        updateInfoProvider.handle(update, botMethodExecutor);
        ApiMethod apiMethod = botMethodExecutor.getApiMethod();
        Assert.assertTrue(apiMethod instanceof SendMessage);
        SendMessage sendMessage = (SendMessage) apiMethod;
        Assert.assertEquals(update.getMessage().getChat().getId().toString(), sendMessage.getChatId());
        Assert.assertEquals(INVALID_PATH_MESSAGE, sendMessage.getText());
    }

    private void testSuccess(String input, Object expected) throws IOException {
        update.getMessage().setText(input);
        updateInfoProvider.handle(update, botMethodExecutor);
        ApiMethod apiMethod = botMethodExecutor.getApiMethod();
        Assert.assertTrue(apiMethod instanceof SendMessage);
        SendMessage sendMessage = (SendMessage) apiMethod;
        Assert.assertEquals(update.getMessage().getChat().getId().toString(), sendMessage.getChatId());
        Assert.assertEquals(mapper.writeValueAsString(expected), sendMessage.getText().substring(3, sendMessage.getText().length() - 3));
    }
}