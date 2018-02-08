package ru.javazen.telegram.bot.handler;

import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.util.HashMap;
import java.util.Map;

public abstract class InstanceForChatController implements UpdateHandler {
    private Map<Long, UpdateHandler> map = new HashMap<>();

    @Override
    public boolean handle(Update update, AbsSender sender) throws TelegramApiException {
        Long chatId = update.getMessage().getChat().getId();
        if (!map.containsKey(chatId)){
            map.put(chatId, newInstance());
        }
        return map.get(chatId).handle(update, sender);
    }

    protected abstract UpdateHandler newInstance();
}
