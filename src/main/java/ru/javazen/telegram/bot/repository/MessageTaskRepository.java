package ru.javazen.telegram.bot.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import ru.javazen.telegram.bot.model.MessageTask;


public interface MessageTaskRepository extends CrudRepository<MessageTask, Long> {

    MessageTask getTaskByChatIdAndMessageId(Long chatId, Long messageId);
}
