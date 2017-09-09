package ru.javazen.telegram.bot.handler;

import ru.javazen.telegram.bot.BotMethodExecutor;
import ru.javazen.telegram.bot.entity.Update;

import java.util.HashMap;
import java.util.Map;

public abstract class InstanceForChatController implements UpdateHandler {
    private Map<Long, UpdateHandler> map = new HashMap<>();

    @Override
    public boolean handle(Update update, BotMethodExecutor executor) {
        Long chatId = update.getMessage().getChat().getId();
        if (!map.containsKey(chatId)){
            map.put(chatId, newInstance());
        }
        return map.get(chatId).handle(update, executor);
    }

    protected abstract UpdateHandler newInstance();
}
