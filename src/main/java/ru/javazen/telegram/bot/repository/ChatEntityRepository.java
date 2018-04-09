package ru.javazen.telegram.bot.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import ru.javazen.telegram.bot.model.ChatEntity;

import java.util.Date;
import java.util.List;

public interface ChatEntityRepository extends CrudRepository<ChatEntity, Long> {
    @Query("select me.chat from MessageEntity me where me.date > :date group by me.chat")
    List<ChatEntity> findActiveChats(@Param("date") Date date);
}
