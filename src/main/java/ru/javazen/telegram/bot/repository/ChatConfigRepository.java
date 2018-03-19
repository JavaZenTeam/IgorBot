package ru.javazen.telegram.bot.repository;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.CrudRepository;
import ru.javazen.telegram.bot.model.ChatConfig;
import ru.javazen.telegram.bot.model.ChatConfigPK;

public interface ChatConfigRepository extends CrudRepository<ChatConfig, ChatConfigPK> {
    @Override
    @CacheEvict(value = "ChatConfig", key = "#root.args[0].chatConfigPK")
    <S extends ChatConfig> S save(S chatConfig);

    @Override
    @Cacheable(value = "ChatConfig")
    ChatConfig findOne(ChatConfigPK chatConfigPK);
}
