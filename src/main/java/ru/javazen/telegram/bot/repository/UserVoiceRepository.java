package ru.javazen.telegram.bot.repository;

import org.springframework.data.repository.CrudRepository;
import ru.javazen.telegram.bot.model.UserVoiceMessage;
import ru.javazen.telegram.bot.model.VoiceMessageEntity;
import ru.javazen.telegram.bot.model.VoiceMessagePK;

import java.util.List;

public interface UserVoiceRepository extends CrudRepository<UserVoiceMessage, VoiceMessagePK> {

    List<VoiceMessageEntity> findTop10ByUserIdOrderByCount(long userId);
}
