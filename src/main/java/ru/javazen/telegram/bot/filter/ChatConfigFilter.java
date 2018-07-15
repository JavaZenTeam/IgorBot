package ru.javazen.telegram.bot.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.telegram.telegrambots.api.objects.Message;
import ru.javazen.telegram.bot.service.ChatConfigService;

public class ChatConfigFilter implements MessageFilter {
    private ChatConfigService chatConfigService;
    private String configKey;
    private String configValue;

    @Override
    public boolean check(Message message) {
        return chatConfigService.getProperty(message.getChatId(), configKey)
                .map(configValue::equals)
                .orElse(false);
    }

    @Autowired
    public void setChatConfigService(ChatConfigService chatConfigService) {
        this.chatConfigService = chatConfigService;
    }

    @Required
    public void setConfigKey(String configKey) {
        this.configKey = configKey;
    }

    @Required
    public void setConfigValue(String configValue) {
        this.configValue = configValue;
    }
}
