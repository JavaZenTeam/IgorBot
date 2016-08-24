package ru.javazen.telegram.bot.handler;

import ru.javazen.telegram.bot.Bot;
import ru.javazen.telegram.bot.entity.request.Update;

import java.util.HashMap;
import java.util.Map;

public abstract class InstanceForChatController implements UpdateHandler {
    private Map<Long, UpdateHandler> map = new HashMap<>();

    @Override
    public boolean handle(Update update, Bot bot) {
        Long chatId = update.getMessage().getChat().getId();
        if (!map.containsKey(chatId)){
            map.put(chatId, newInstance());
        }
        return map.get(chatId).handle(update, bot);
    }

    protected abstract UpdateHandler newInstance();
}
