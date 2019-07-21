package ru.javazen.telegram.bot.repository;

import org.springframework.data.repository.Repository;
import org.springframework.scheduling.annotation.Async;
import ru.javazen.telegram.bot.model.MessageEntity;
import ru.javazen.telegram.bot.model.MessagePK;

import java.util.Date;
import java.util.concurrent.CompletableFuture;

public interface MessageRepository extends Repository<MessageEntity, MessagePK> {
    @Async
    <S extends MessageEntity> CompletableFuture<S> save(S message);

    MessageEntity findTop1ByChatChatIdOrderByDateAsc(Long chatId);

    default Date startChatDate(Long chatId) {
        return findTop1ByChatChatIdOrderByDateAsc(chatId).getDate();
    }
}
