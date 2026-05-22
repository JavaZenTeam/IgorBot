package ru.javazen.telegram.bot.handler;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.javazen.telegram.bot.util.SizedItemsContainer;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ToastHandlerTest {

    private ToastHandler handler;
    private SizedItemsContainer<String> container;

    @Before
    public void setUp() {
        container = new SizedItemsContainer<>();
        container.put("За встречу!", 1.0);
        container.put("Будем здоровы!", 1.0);

        handler = new ToastHandler();
        handler.setContainer(container);
    }

    @Test
    public void testHandleSendsToast() throws TelegramApiException {
        Message message = mock(Message.class);
        when(message.getChatId()).thenReturn(12345L);
        when(message.getMessageThreadId()).thenReturn(null);

        AbsSender sender = mock(AbsSender.class);

        boolean result = handler.handle(message, "выпьем", sender);

        assertTrue(result);
        ArgumentCaptor<SendMessage> captor = ArgumentCaptor.forClass(SendMessage.class);
        verify(sender).execute(captor.capture());
        SendMessage sent = captor.getValue();
        assertEquals("12345", sent.getChatId());
        assertTrue(sent.getText().equals("За встречу!") || sent.getText().equals("Будем здоровы!"));
    }

    @Test
    public void testHandleEmptyContainerReturnsFalse() throws TelegramApiException {
        ToastHandler emptyHandler = new ToastHandler();
        SizedItemsContainer<String> emptyContainer = new SizedItemsContainer<>();
        emptyHandler.setContainer(emptyContainer);

        Message message = mock(Message.class);
        AbsSender sender = mock(AbsSender.class);

        boolean result = emptyHandler.handle(message, "выпьем", sender);
        assertFalse(result);
        verify(sender, never()).execute(any(SendMessage.class));
    }
}
