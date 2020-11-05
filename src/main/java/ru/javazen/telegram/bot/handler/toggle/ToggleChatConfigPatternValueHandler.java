package ru.javazen.telegram.bot.handler.toggle;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.javazen.telegram.bot.handler.base.TextMessageHandler;
import ru.javazen.telegram.bot.service.ChatConfigService;

import java.text.MessageFormat;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ToggleChatConfigPatternValueHandler implements TextMessageHandler {
    private static final String CONFIG_VALUE_GROUP_NAME = "configValue";

    private ChatConfigService chatConfigService;
    private String configKey;
    private Pattern pattern;
    private Supplier<String> responseSupplier;

    @Override
    public boolean handle(Message message, String text, AbsSender sender) throws TelegramApiException {
        Matcher matcher = pattern.matcher(text);
        if (!matcher.find()) return false;

        String configValue = matcher.group(CONFIG_VALUE_GROUP_NAME);

        chatConfigService.setProperty(message.getChatId(), configKey, configValue);

        String response = MessageFormat.format(responseSupplier.get(), configValue);
        sender.execute(new SendMessage(message.getChatId(), response));
        return true;
    }

    @Autowired
    public void setChatConfigService(ChatConfigService chatConfigService) {
        this.chatConfigService = chatConfigService;
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
