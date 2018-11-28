package ru.javazen.telegram.bot.datasource;

import ru.javazen.telegram.bot.datasource.model.CountStatistic;
import ru.javazen.telegram.bot.datasource.model.UserStatistic;

import java.util.Date;
import java.util.List;

public interface ChatDataSource {
    List<UserStatistic> topActiveUsers(Long chatId, Date after, Date before);
    List<CountStatistic> topStickers(Long chatId, Date after, Date before);

    List<CountStatistic> botUsagesByModule(Long chatId, Date after, Date before);

    List<CountStatistic> wordsUsageStatistic(Long chatId, Date after, Date before);

    Long messagesCount(Long chatId, Date after, Date before);
}
