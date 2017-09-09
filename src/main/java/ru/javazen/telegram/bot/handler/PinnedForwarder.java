package ru.javazen.telegram.bot.handler;

import ru.javazen.telegram.bot.BotMethodExecutor;
import ru.javazen.telegram.bot.entity.Message;
import ru.javazen.telegram.bot.entity.Update;
import ru.javazen.telegram.bot.method.send.ForwardMessage;

import java.util.Collection;

/**
 * Created by egor on 18.05.2016.
 * kto prochital, tot andrey
 */

public class PinnedForwarder implements UpdateHandler {

    private Collection<String> storeFromChatIds;

    private String storeChatId;

    @Override
    public boolean handle(Update update, BotMethodExecutor executor) {
        Message pinnedMessage = update.getMessage().getPinnedMessage();
        if (pinnedMessage == null
                || !storeFromChatIds.contains(Long.toString(pinnedMessage.getChat().getId()))) {
            return false;
        }

        ForwardMessage forwardMessage = new ForwardMessage();
        forwardMessage.setFromChatId(Long.toString(update.getMessage().getChat().getId()));
        forwardMessage.setMessageId(update.getMessage().getPinnedMessage().getMessageId());
        forwardMessage.setChatId(storeChatId);
        executor.execute(forwardMessage, Void.class);

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
