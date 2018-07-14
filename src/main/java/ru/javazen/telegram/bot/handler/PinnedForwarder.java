package ru.javazen.telegram.bot.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.api.methods.ForwardMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ru.javazen.telegram.bot.handling.annotation.Handling;
import ru.javazen.telegram.bot.handling.annotation.UpdateType;
import ru.javazen.telegram.bot.service.ChatConfigService;

/**
 * Created by egor on 18.05.2016.
 * kto prochital, tot andrey
 */
@Handling(UpdateType.MESSAGE)
public class PinnedForwarder implements UpdateHandler {
    private ChatConfigService chatConfigService;
    private String configKey;

    @Override
    public boolean handle(Update update, AbsSender sender) throws TelegramApiException {
        Message pinnedMessage = update.getMessage().getPinnedMessage();
        if (pinnedMessage == null) return false;

        String targetChatId = chatConfigService.getProperty(update.getMessage().getChatId(), configKey)
                .orElse(null);
        if (targetChatId == null) return false;

        ForwardMessage forwardMessage = new ForwardMessage();
        forwardMessage.setFromChatId(Long.toString(update.getMessage().getChat().getId()));
        forwardMessage.setMessageId(update.getMessage().getPinnedMessage().getMessageId());
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
