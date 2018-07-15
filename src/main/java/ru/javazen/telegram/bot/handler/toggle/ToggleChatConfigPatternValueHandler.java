package ru.javazen.telegram.bot.handler.toggle;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ru.javazen.telegram.bot.handler.base.TextMessageHandler;
import ru.javazen.telegram.bot.model.ChatConfig;
import ru.javazen.telegram.bot.repository.ChatConfigRepository;

import java.text.MessageFormat;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ToggleChatConfigPatternValueHandler implements TextMessageHandler {
    private static final String CONFIG_VALUE_GROUP_NAME = "configValue";

    private ChatConfigRepository chatConfigRepository;
    private String configKey;
    private Pattern pattern;
    private Supplier<String> responseSupplier;

    @Override
    public boolean handle(Message message, String text, AbsSender sender) throws TelegramApiException {
        Matcher matcher = pattern.matcher(text);
        if (!matcher.find()) return false;

        String configValue = matcher.group(CONFIG_VALUE_GROUP_NAME);

        ChatConfig config = new ChatConfig(message.getChatId(), configKey, configValue);
        chatConfigRepository.save(config);

        String response = MessageFormat.format(responseSupplier.get(), configValue);
        sender.execute(new SendMessage(message.getChatId(), response));
        return true;
    }

    @Autowired
    public void setChatConfigRepository(ChatConfigRepository chatConfigRepository) {
        this.chatConfigRepository = chatConfigRepository;
    }

    public void setConfigKey(String configKey) {
        this.configKey = configKey;
    }

    public void setPattern(Pattern pattern) {
        this.pattern = pattern;
    }

    public void setResponse(String response) {
        this.responseSupplier = () -> response;
    }

    public void setResponseSupplier(Supplier<String> responseSupplier) {
        this.responseSupplier = responseSupplier;
    }
}
