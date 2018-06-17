package ru.javazen.telegram.bot.service.impl;

import org.springframework.stereotype.Service;
import ru.javazen.telegram.bot.model.ChatConfig;
import ru.javazen.telegram.bot.model.ChatConfigPK;
import ru.javazen.telegram.bot.repository.ChatConfigRepository;
import ru.javazen.telegram.bot.service.ChatConfigService;

import java.util.Optional;

@Service
public class ChatConfigServiceImpl implements ChatConfigService {

    private final ChatConfigRepository chatConfigRepository;

    public ChatConfigServiceImpl(ChatConfigRepository chatConfigRepository) {
        this.chatConfigRepository = chatConfigRepository;
    }

    @Override
    public Optional<String> getProperty(long chatId, String key) {
        ChatConfigPK chatConfigPK = new ChatConfigPK(chatId, key);
        Optional<ChatConfig> chatConfig = chatConfigRepository.findByChatConfigPK(chatConfigPK);

        return chatConfig.map(ChatConfig::getValue);
    }

    @Override
    public void setProperty(long chatId, String key, String value) {
        ChatConfig chatConfig = new ChatConfig(chatId, key, value);
        chatConfigRepository.save(chatConfig);
    }
}
