package ru.javazen.telegram.bot.handler;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.javazen.telegram.bot.handler.base.MessageHandler;

import java.util.HashMap;
import java.util.Map;

public abstract class InstanceForChatController<T extends MessageHandler> implements MessageHandler {
    private Map<Long, T> map = new HashMap<>();

    @Override
    public boolean handle(Message message, AbsSender sender) throws TelegramApiException {
        if (!map.containsKey(message.getChatId())) {
            map.put(message.getChatId(), newInstance());
        }
        return map.get(message.getChatId()).handle(message, sender);
    }

    @Override
    public String getName() {
        T instance = map.isEmpty()
                ? newInstance()
                : map.values().iterator().next();
        return instance.getName();
    }

    protected abstract T newInstance();
}
