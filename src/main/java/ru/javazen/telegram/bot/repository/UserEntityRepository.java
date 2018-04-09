package ru.javazen.telegram.bot.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import ru.javazen.telegram.bot.model.ChatEntity;
import ru.javazen.telegram.bot.model.UserEntity;

import java.util.Date;
import java.util.List;

public interface UserEntityRepository extends CrudRepository<UserEntity, Integer> {
    @Query("select me.user from MessageEntity me where me.date > :date group by me.user")
    List<UserEntity> findActiveUsers(@Param("date") Date date);
}

