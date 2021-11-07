package ru.javazen.telegram.bot.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import ru.javazen.telegram.bot.model.DailyUserChatPK;
import ru.javazen.telegram.bot.model.DailyUserChatStatistic;

public interface DailyUserChatStatisticRepository extends Repository<DailyUserChatStatistic, DailyUserChatPK> {
    @Modifying
    @Query(value = "REFRESH MATERIALIZED VIEW CONCURRENTLY daily_user_chat_statistic", nativeQuery = true)
    void refreshView();
}
