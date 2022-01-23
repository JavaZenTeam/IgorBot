package ru.javazen.telegram.bot.repository;

import org.springframework.data.repository.Repository;
import org.springframework.scheduling.annotation.Async;
import ru.javazen.telegram.bot.model.MessageEntity;
import ru.javazen.telegram.bot.model.MessagePK;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface MessageRepository extends Repository<MessageEntity, MessagePK> {
    @Async
    <S extends MessageEntity> CompletableFuture<S> save(S message);

    List<MessageEntity> findAllByChatChatIdAndEventTypeIsNotNullAndDateBetweenOrderByDateAsc(Long chatId, Date from, Date to);

    MessageEntity findTop1ByChatChatIdOrderByDateAsc(Long chatId);

    MessageEntity findTop1ByOrderByDateAsc();

    default List<MessageEntity> findEventsByChatId(Long chatId, Date from, Date to) {
        return findAllByChatChatIdAndEventTypeIsNotNullAndDateBetweenOrderByDateAsc(chatId, from, to);
    }

    default Date startChatDate(Long chatId) {
        return findTop1ByChatChatIdOrderByDateAsc(chatId).getDate();
    }
    default Date startBotDate() {
        return findTop1ByOrderByDateAsc().getDate();
    }
}
