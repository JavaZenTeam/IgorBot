package ru.javazen.telegram.bot.handler.toggle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ru.javazen.telegram.bot.handler.base.TextMessageHandler;
import ru.javazen.telegram.bot.model.ChatConfig;
import ru.javazen.telegram.bot.repository.ChatConfigRepository;

import java.util.Map;
import java.util.function.Supplier;

public class ToggleChatConfigMappedValuesHandler implements TextMessageHandler {
    private String configKey;
    private Map<String, String> pattern2value;
    private Map<String, Supplier<String>> value2response;
    private ChatConfigRepository chatConfigRepository;

    @Override
    public boolean handle(Message message, String text, AbsSender sender) throws TelegramApiException {
        String configValue = pattern2value.get(text);
        if (configValue == null) return false;

        ChatConfig config = new ChatConfig(message.getChatId(), configKey, configValue);
        chatConfigRepository.save(config);

        String response = value2response.getOrDefault(configValue, () -> configKey + "=" + configValue).get();
        sender.execute(new SendMessage(message.getChatId(), response));
        return true;
    }

    @Override
    public String getName() {
        return "ToggleChatConfig:" + configKey;
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
