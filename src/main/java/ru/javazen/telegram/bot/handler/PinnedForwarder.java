package ru.javazen.telegram.bot.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.methods.ForwardMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.javazen.telegram.bot.handler.base.MessageHandler;
import ru.javazen.telegram.bot.service.ChatConfigService;

/**
 * Created by egor on 18.05.2016.
 * kto prochital, tot andrey
 */

public class PinnedForwarder implements MessageHandler {
    private ChatConfigService chatConfigService;
    private String configKey;

    @Override
    public boolean handle(Message message, AbsSender sender) throws TelegramApiException {
        Message pinnedMessage = message.getPinnedMessage();
        if (pinnedMessage == null) return false;

        String targetChatId = chatConfigService.getProperty(message.getChatId(), configKey).orElse(null);
        if (targetChatId == null) return false;

        ForwardMessage forwardMessage = new ForwardMessage();
        forwardMessage.setFromChatId(message.getChatId().toString());
        forwardMessage.setMessageId(message.getPinnedMessage().getMessageId());
        forwardMessage.setChatId(targetChatId);
        sender.execute(forwardMessage);

        return true;
    }

    @Autowired
    public void setChatConfigService(ChatConfigService chatConfigService) {
        this.chatConfigService = chatConfigService;
    }

    public void setConfigKey(String configKey) {
        this.configKey = configKey;
    }
}
