package ru.javazen.telegram.bot.scheduler;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatAdministrators;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.javazen.telegram.bot.scheduler.service.MessageSchedulerService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UnschedulerNotifyHandlerTest {

    private static final Long CHAT_ID = 222L;
    private static final Integer TASK_MESSAGE_ID = 333;

    private UnschedulerNotifyHandler handler;

    @Mock
    private MessageSchedulerService messageSchedulerService;

    @Mock
    private AbsSender sender;

    @Mock
    private Message message;

    @Mock
    private Message replyToMessage;

    @Before
    public void init() {
        handler = new UnschedulerNotifyHandler(messageSchedulerService, () -> "ok");

        when(message.isReply()).thenReturn(true);
        when(message.getChatId()).thenReturn(CHAT_ID);
        when(message.getReplyToMessage()).thenReturn(replyToMessage);
        when(replyToMessage.getMessageId()).thenReturn(TASK_MESSAGE_ID);
    }

    @Test
    public void replyAuthorCanCancelTask() throws TelegramApiException {
        setMessageAuthor(111L);
        setReplyAuthor(111L);
        when(messageSchedulerService.cancelTaskByChatAndMessage(CHAT_ID, TASK_MESSAGE_ID)).thenReturn(true);

        assertTrue(handler.handle(message, "", sender));

        verify(messageSchedulerService).cancelTaskByChatAndMessage(CHAT_ID, TASK_MESSAGE_ID);
        verify(sender, never()).execute(any(GetChatAdministrators.class));
    }

    @Test
    public void chatAdminCanCancelAnotherAuthorTask() throws TelegramApiException {
        setMessageAuthor(111L);
        setReplyAuthor(999L);
        when(sender.execute(any(GetChatAdministrators.class))).thenReturn(new ArrayList<>(List.of(admin(111L))));
        when(messageSchedulerService.cancelTaskByChatAndMessage(CHAT_ID, TASK_MESSAGE_ID)).thenReturn(true);

        assertTrue(handler.handle(message, "", sender));

        verify(messageSchedulerService).cancelTaskByChatAndMessage(CHAT_ID, TASK_MESSAGE_ID);
    }

    @Test
    public void personalChatUserCanCancelAnotherAuthorTask() throws TelegramApiException {
        setMessageAuthor(111L);
        setReplyAuthor(999L);
        setPersonalChat();
        when(messageSchedulerService.cancelTaskByChatAndMessage(CHAT_ID, TASK_MESSAGE_ID)).thenReturn(true);

        assertTrue(handler.handle(message, "", sender));

        verify(messageSchedulerService).cancelTaskByChatAndMessage(CHAT_ID, TASK_MESSAGE_ID);
        verify(sender, never()).execute(any(GetChatAdministrators.class));
    }

    @Test
    public void nonAdminCanNotCancelAnotherAuthorTask() throws TelegramApiException {
        setMessageAuthor(111L);
        setReplyAuthor(999L);
        when(sender.execute(any(GetChatAdministrators.class))).thenReturn(new ArrayList<>());

        assertFalse(handler.handle(message, "", sender));

        verify(messageSchedulerService, never()).cancelTaskByChatAndMessage(anyLong(), anyInt());
    }

    private void setPersonalChat() {
        Chat chat = mock(Chat.class);
        when(chat.isUserChat()).thenReturn(true);
        when(message.getChat()).thenReturn(chat);
    }

    private void setMessageAuthor(Long userId) {
        when(message.getFrom()).thenReturn(user(userId));
    }

    private void setReplyAuthor(Long userId) {
        when(replyToMessage.getFrom()).thenReturn(user(userId));
    }

    private ChatMember admin(Long userId) {
        ChatMember admin = mock(ChatMember.class);
        when(admin.getUser()).thenReturn(user(userId));
        return admin;
    }

    private User user(Long id) {
        User user = mock(User.class);
        when(user.getId()).thenReturn(id);
        return user;
    }
}
