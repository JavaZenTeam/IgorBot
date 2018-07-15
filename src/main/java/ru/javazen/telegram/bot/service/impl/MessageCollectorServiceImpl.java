package ru.javazen.telegram.bot.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import ru.javazen.telegram.bot.model.*;
import ru.javazen.telegram.bot.repository.BotUsageLogRepository;
import ru.javazen.telegram.bot.repository.ChatConfigRepository;
import ru.javazen.telegram.bot.repository.MessageRepository;
import ru.javazen.telegram.bot.service.MessageCollectorService;

import java.util.Objects;

public class MessageCollectorServiceImpl implements MessageCollectorService {
    private ChatConfigRepository chatConfigRepository;
    private MessageRepository messageRepository;
    private BotUsageLogRepository botUsageLogRepository;
    private String saveTextKey;
    private String saveTextValue;

    @Override
    public void saveUpdate(Update update) {
        if (update.getMessage() == null) return; //TODO save not only messages

        MessageEntity entity = new MessageEntity(update.getMessage());
        if (!saveTextAllowed(update.getMessage())) {
            entity.setText(null);
        }
        messageRepository.save(entity);
    }

    @Override
    public void saveBotUsage(Update update, Message botResponse, String handlerName) {
        Message message = update.getMessage();
        if (message == null) return; //TODO save not only messages

        BotUsageLog botUsageLog = new BotUsageLog();
        botUsageLog.setTarget(new MessagePK(botResponse.getChatId(), botResponse.getMessageId()));
        if (saveTextAllowed(message)) {
            botUsageLog.setText(botResponse.getText());
        }
        botUsageLog.setSource(new MessagePK(message.getChatId(), message.getMessageId()));
        botUsageLog.setModuleName(handlerName);
        botUsageLogRepository.save(botUsageLog);
    }

    private boolean saveTextAllowed(Message userMessage) {
        ChatConfigPK chatConfigPK = new ChatConfigPK(userMessage.getChatId(), saveTextKey);
        String chatSaveTextValue = chatConfigRepository.findByChatConfigPK(chatConfigPK).map(ChatConfig::getValue).orElse(null);
        return Objects.equals(chatSaveTextValue, saveTextValue);
    }

    @Autowired
    public void setChatConfigRepository(ChatConfigRepository chatConfigRepository) {
        this.chatConfigRepository = chatConfigRepository;
    }

    @Autowired
    public void setMessageRepository(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @Autowired
    public void setBotUsageLogRepository(BotUsageLogRepository botUsageLogRepository) {
        this.botUsageLogRepository = botUsageLogRepository;
    }

    public void setSaveTextKey(String saveTextKey) {
        this.saveTextKey = saveTextKey;
    }

    public void setSaveTextValue(String saveTextValue) {
        this.saveTextValue = saveTextValue;
    }
}
