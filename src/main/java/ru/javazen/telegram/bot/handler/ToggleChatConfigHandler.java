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
import java.util.function.Supplier;

public class ToggleChatConfigHandler implements UpdateHandler {
    private String configKey;
    private Map<String, String> pattern2value;
    private Map<String, Supplier<String>> value2response;
    private ChatConfigRepository chatConfigRepository;

    @Override
    public boolean handle(Update update, AbsSender sender) throws TelegramApiException {
        String text = MessageHelper.getActualText(update.getMessage());
        String configValue = pattern2value.get(text);
        if (configValue == null) return false;

        ChatConfig config = new ChatConfig(update.getMessage().getChatId(), configKey, configValue);
        chatConfigRepository.save(config);

        String response = value2response.getOrDefault(configValue, () -> configKey + "=" + configValue).get();
        sender.execute(MessageHelper.answer(update.getMessage(), response));
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

    public void setValue2response(Map<String, Supplier<String>> value2response) {
        this.value2response = value2response;
    }

    @Autowired
    public void setChatConfigRepository(ChatConfigRepository chatConfigRepository) {
        this.chatConfigRepository = chatConfigRepository;
    }
}
