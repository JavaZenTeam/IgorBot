package ru.javazen.telegram.bot.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ru.javazen.telegram.bot.model.ChatConfig;
import ru.javazen.telegram.bot.model.ChatConfigPK;
import ru.javazen.telegram.bot.model.MessageEntity;
import ru.javazen.telegram.bot.model.MessagePK;
import ru.javazen.telegram.bot.repository.ChatConfigRepository;
import ru.javazen.telegram.bot.repository.MessageRepository;
import ru.javazen.telegram.bot.util.MessageHelper;

import java.util.Date;
import java.util.Objects;

public class MessageCollector implements UpdateHandler {
    private ChatConfigRepository chatConfigRepository;
    private MessageRepository messageRepository;
    private String saveTextKey;
    private String saveTextValue;

    @Override
    public boolean handle(Update update, AbsSender sender) throws TelegramApiException {
        MessageEntity entity = new MessageEntity();
        Message message = update.getMessage();
        entity.setMessagePK(new MessagePK(message.getChatId(), message.getMessageId()));
        entity.setDate(new Date(1000 * message.getDate()));
        entity.setUserId(message.getFrom().getId());

        if (saveTextKey != null){
            ChatConfig config = chatConfigRepository.findOne(new ChatConfigPK(message.getChatId(), saveTextKey));
            if (config != null && Objects.equals(config.getValue(), saveTextValue)){
                entity.setText(MessageHelper.getActualText(message));
            }
        }

        messageRepository.save(entity);
        return false;
    }

    @Autowired
    public void setChatConfigRepository(ChatConfigRepository chatConfigRepository) {
        this.chatConfigRepository = chatConfigRepository;
    }

    @Autowired
    public void setMessageRepository(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public void setSaveTextKey(String saveTextKey) {
        this.saveTextKey = saveTextKey;
    }

    public void setSaveTextValue(String saveTextValue) {
        this.saveTextValue = saveTextValue;
    }
}
