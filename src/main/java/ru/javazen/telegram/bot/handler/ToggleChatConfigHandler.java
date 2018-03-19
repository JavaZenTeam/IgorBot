package ru.javazen.telegram.bot.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ru.javazen.telegram.bot.model.ChatConfig;
import ru.javazen.telegram.bot.repository.ChatConfigRepository;
import ru.javazen.telegram.bot.util.MessageHelper;

import java.util.Map;

public class ToggleChatConfigHandler implements UpdateHandler {
    private String configKey;
    private Map<String, String> pattern2value;
    private ChatConfigRepository chatConfigRepository;

    @Override
    public boolean handle(Update update, AbsSender sender) throws TelegramApiException {
        String text = MessageHelper.getActualText(update.getMessage());
        String configValue = pattern2value.get(text);
        if (configValue == null) return false;

        ChatConfig config = new ChatConfig(update.getMessage().getChatId(), configKey, configValue);
        chatConfigRepository.save(config);
        return true;
    }

    @Required
    public void setConfigKey(String configKey) {
        this.configKey = configKey;
    }

    @Required
    public void setPattern2value(Map<String, String> pattern2value) {
        this.pattern2value = pattern2value;
    }

    @Autowired
    public void setChatConfigRepository(ChatConfigRepository chatConfigRepository) {
        this.chatConfigRepository = chatConfigRepository;
    }
}
