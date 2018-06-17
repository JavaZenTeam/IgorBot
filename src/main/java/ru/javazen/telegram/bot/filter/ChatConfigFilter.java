package ru.javazen.telegram.bot.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.telegram.telegrambots.api.objects.Update;
import ru.javazen.telegram.bot.model.ChatConfig;
import ru.javazen.telegram.bot.model.ChatConfigPK;
import ru.javazen.telegram.bot.repository.ChatConfigRepository;

import java.util.Objects;

public class ChatConfigFilter implements Filter {
    private ChatConfigRepository chatConfigRepository;
    private String configKey;
    private String configValue;

    @Override
    public boolean check(Update update) {
        String value = chatConfigRepository.findByChatConfigPK(new ChatConfigPK(update.getMessage().getChatId(), configKey))
                .map(ChatConfig::getValue).orElse(null);
        return Objects.equals(value, configValue);
    }

    @Autowired
    public void setChatConfigRepository(ChatConfigRepository chatConfigRepository) {
        this.chatConfigRepository = chatConfigRepository;
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
