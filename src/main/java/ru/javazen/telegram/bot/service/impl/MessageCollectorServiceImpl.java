package ru.javazen.telegram.bot.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import ru.javazen.telegram.bot.model.BotUsageLog;
import ru.javazen.telegram.bot.model.MessageEntity;
import ru.javazen.telegram.bot.model.MessagePK;
import ru.javazen.telegram.bot.repository.BotUsageLogRepository;
import ru.javazen.telegram.bot.repository.MessageRepository;
import ru.javazen.telegram.bot.service.ChatConfigService;
import ru.javazen.telegram.bot.service.MessageCollectorService;

public class MessageCollectorServiceImpl implements MessageCollectorService {
    private ChatConfigService chatConfigService;
    private MessageRepository messageRepository;
    private BotUsageLogRepository botUsageLogRepository;
    private ModelMapper modelMapper;
    private String saveTextKey;
    private String saveTextValue;

    @Override
    public void saveUpdate(Update update) {
        if (update.getMessage() != null) {
            saveMessage(update.getMessage());
        }
        //TODO save not only messages
    }

    @Override
    public void saveMessage(Message message) {
        MessageEntity entity = modelMapper.map(message, MessageEntity.class);
        if (hideText(message)) {
            entity.setText(null);
        }
        messageRepository.save(entity);
    }

    @Override
    public void saveBotUsage(Update update, Message botResponse, String handlerName) {
        Message message = update.getMessage();
        if (message == null) return; //TODO save not only messages

        BotUsageLog botUsageLog = new BotUsageLog();
        botUsageLog.setTarget(modelMapper.map(botResponse, MessagePK.class));
        botUsageLog.setSource(modelMapper.map(update.getMessage(), MessagePK.class));
        if (hideText(message)) {
            botUsageLog.setText(botResponse.getText());
        }
        botUsageLog.setModuleName(handlerName);
        botUsageLogRepository.save(botUsageLog);
    }

    private boolean hideText(Message userMessage) {
        return chatConfigService.getProperty(userMessage.getChatId(), saveTextKey)
                .map(saveTextValue::equals).orElse(false);
    }

    @Autowired
    public void setChatConfigService(ChatConfigService chatConfigService) {
        this.chatConfigService = chatConfigService;
    }

    @Autowired
    public void setMessageRepository(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @Autowired
    public void setBotUsageLogRepository(BotUsageLogRepository botUsageLogRepository) {
        this.botUsageLogRepository = botUsageLogRepository;
    }

    @Autowired
    public void setModelMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public void setSaveTextKey(String saveTextKey) {
        this.saveTextKey = saveTextKey;
    }

    public void setSaveTextValue(String saveTextValue) {
        this.saveTextValue = saveTextValue;
    }
}
