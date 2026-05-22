package ru.javazen.telegram.bot.handler;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.javazen.telegram.bot.handler.base.TextMessageHandler;
import ru.javazen.telegram.bot.util.SizedItemsContainer;

import java.util.Map;
import java.util.Random;

public class ToastHandler implements TextMessageHandler {
    private final Random random = new Random();
    private SizedItemsContainer<String> container;

    @Override
    public boolean handle(Message message, String text, AbsSender sender) throws TelegramApiException {
        if (container == null || container.size() == 0) return false;

        String toast = container.get(random.nextDouble() * container.size());
        if (toast == null) return false;

        SendMessage sendMessage = new SendMessage(message.getChatId().toString(), toast);
        sendMessage.setMessageThreadId(message.getMessageThreadId());
        sender.execute(sendMessage);
        return true;
    }

    public void setContainer(SizedItemsContainer<String> container) {
        this.container = container;
    }

    public void setToasts(Map<String, Double> toasts) {
        container = new SizedItemsContainer<>();
        toasts.forEach(container::put);
    }
}
