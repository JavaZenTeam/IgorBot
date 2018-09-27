package ru.javazen.telegram.bot.datasource;

import ru.javazen.telegram.bot.datasource.model.BotUsageStatistic;
import ru.javazen.telegram.bot.datasource.model.UserStatistic;

import java.util.Date;
import java.util.List;

public interface ChatDataSource {
    List<UserStatistic> topActiveUsers(Long chatId, Date after, Date before);

    List<BotUsageStatistic> botUsagesByModule(Long chatId, Date after, Date before);
}
