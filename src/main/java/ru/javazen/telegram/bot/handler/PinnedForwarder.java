package ru.javazen.telegram.bot.handler;

import org.telegram.telegrambots.api.methods.ForwardMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.util.Collection;

/**
 * Created by egor on 18.05.2016.
 * kto prochital, tot andrey
 */

public class PinnedForwarder implements UpdateHandler {

    private Collection<String> storeFromChatIds;

    private String storeChatId;

    @Override
    public boolean handle(Update update, AbsSender sender) throws TelegramApiException {
        Message pinnedMessage = update.getMessage().getPinnedMessage();
        if (pinnedMessage == null
                || !storeFromChatIds.contains(Long.toString(pinnedMessage.getChat().getId()))) {
            return false;
        }

        ForwardMessage forwardMessage = new ForwardMessage();
        forwardMessage.setFromChatId(Long.toString(update.getMessage().getChat().getId()));
        forwardMessage.setMessageId(update.getMessage().getPinnedMessage().getMessageId());
        forwardMessage.setChatId(storeChatId);
        sender.execute(forwardMessage);

        return true;
    }

    public Collection<String> getStoreFromChatIds() {
        return storeFromChatIds;
    }

    public void setStoreFromChatIds(Collection<String> storeFromChatIds) {
        this.storeFromChatIds = storeFromChatIds;
    }

    public String getStoreChatId() {
        return storeChatId;
    }

    public void setStoreChatId(String storeChatId) {
        this.storeChatId = storeChatId;
    }
}
