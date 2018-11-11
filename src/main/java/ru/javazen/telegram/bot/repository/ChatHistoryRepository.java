package ru.javazen.telegram.bot.repository;

import org.springframework.data.repository.CrudRepository;
import ru.javazen.telegram.bot.model.ChatHistory;

public interface ChatHistoryRepository extends CrudRepository<ChatHistory, Long> {

}
