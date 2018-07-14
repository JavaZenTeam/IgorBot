package ru.javazen.telegram.bot.handler;

import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ru.javazen.telegram.bot.handling.annotation.Handling;
import ru.javazen.telegram.bot.handling.annotation.UpdateType;

import java.util.HashMap;
import java.util.Map;

@Handling(UpdateType.MESSAGE)
public abstract class InstanceForChatController<T extends UpdateHandler> implements UpdateHandler {
    private Map<Long, T> map = new HashMap<>();

    @Override
    public boolean handle(Update update, AbsSender sender) throws TelegramApiException {
        Long chatId = update.getMessage().getChat().getId();
        if (!map.containsKey(chatId)){
            map.put(chatId, newInstance());
        }
        return map.get(chatId).handle(update, sender);
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
