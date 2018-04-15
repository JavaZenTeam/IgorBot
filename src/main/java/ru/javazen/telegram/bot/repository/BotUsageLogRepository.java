package ru.javazen.telegram.bot.repository;

import org.springframework.data.repository.Repository;
import org.springframework.scheduling.annotation.Async;
import ru.javazen.telegram.bot.model.BotUsageLog;
import ru.javazen.telegram.bot.model.MessagePK;

import java.util.concurrent.CompletableFuture;

public interface BotUsageLogRepository extends Repository<BotUsageLog, MessagePK> {
    @Async
    @SuppressWarnings("UnusedReturnValue")
    <S extends BotUsageLog> CompletableFuture<S> save(S botUsageLog);
}
