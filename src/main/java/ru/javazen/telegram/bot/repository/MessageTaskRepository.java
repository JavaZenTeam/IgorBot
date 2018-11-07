package ru.javazen.telegram.bot.repository;

import org.springframework.data.repository.CrudRepository;
import ru.javazen.telegram.bot.model.MessageTask;


public interface MessageTaskRepository extends CrudRepository<MessageTask, Long> {

    MessageTask getTaskByChatIdAndMessageId(Long chatId, Long messageId);
}
