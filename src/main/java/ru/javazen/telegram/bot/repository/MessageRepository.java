package ru.javazen.telegram.bot.repository;

import org.springframework.data.repository.Repository;
import org.springframework.scheduling.annotation.Async;
import ru.javazen.telegram.bot.model.MessageEntity;
import ru.javazen.telegram.bot.model.MessagePK;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface MessageRepository extends Repository<MessageEntity, MessagePK> {
    @Async
    <S extends MessageEntity> CompletableFuture<S> save(S message);

    List<MessageEntity> findAllByChatChatIdAndEventTypeIsNotNullAndDateBetweenOrderByDateAsc(Long chatId, LocalDateTime from, LocalDateTime to);

    MessageEntity findTop1ByChatChatIdOrderByDateAsc(Long chatId);

    MessageEntity findTop1ByOrderByDateAsc();

    default List<MessageEntity> findEventsByChatId(Long chatId, LocalDateTime from, LocalDateTime to) {
        return findAllByChatChatIdAndEventTypeIsNotNullAndDateBetweenOrderByDateAsc(chatId, from, to);
    }

    /**
     * Возвращает дату первого сообщения в чате.
     * Date хранится в БД в UTC (согласно application.yml: hibernate.jdbc.time_zone: UTC).
     * Конвертация в timezone пользователя должна происходить в контроллере.
     */
    default Date startChatDate(Long chatId) {
        MessageEntity message = findTop1ByChatChatIdOrderByDateAsc(chatId);
        return message != null ? message.getDate() : null;
    }
    
    /**
     * Возвращает дату первого сообщения бота.
     * Date хранится в БД в UTC (согласно application.yml: hibernate.jdbc.time_zone: UTC).
     * Конвертация в timezone пользователя должна происходить в контроллере.
     */
    default Date startBotDate() {
        MessageEntity message = findTop1ByOrderByDateAsc();
        return message != null ? message.getDate() : null;
    }
}
