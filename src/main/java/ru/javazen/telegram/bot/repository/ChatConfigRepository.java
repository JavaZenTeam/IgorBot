package ru.javazen.telegram.bot.repository;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.Repository;
import ru.javazen.telegram.bot.model.ChatConfig;
import ru.javazen.telegram.bot.model.ChatConfigPK;

import java.util.Optional;

public interface ChatConfigRepository extends Repository<ChatConfig, ChatConfigPK> {
    @CacheEvict(value = "ChatConfig", key = "#root.args[0].chatConfigPK")
    <S extends ChatConfig> S save(S chatConfig);

    @Cacheable(value = "ChatConfig")
    Optional<ChatConfig> findByChatConfigPK(ChatConfigPK chatConfigPK);
}