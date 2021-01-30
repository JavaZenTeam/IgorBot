package ru.javazen.telegram.bot.datasource.model;

import lombok.Getter;
import ru.javazen.telegram.bot.model.ChatEntity;
import ru.javazen.telegram.bot.model.UserEntity;

public interface PeriodStatistic<T> extends Statistic<T> {
    String getPeriod();

    @Getter
    class UserPeriodStatistic extends Statistic.UserStatistic implements PeriodStatistic<UserEntity> {
        private final String period;

        public UserPeriodStatistic(String period, UserEntity subject, Long count, Long length, Double score) {
            super(subject, count, length, score);
            this.period = period;
        }

        public UserPeriodStatistic(String period) {
            super(null, null, null, null);
            this.period = period;
        }
    }

    @Getter
    class ChatPeriodStatistic extends Statistic.ChatStatistic implements PeriodStatistic<ChatEntity> {
        private final String period;

        public ChatPeriodStatistic(String period, ChatEntity subject, Long count, Long length, Double score) {
            super(subject, count, length, score);
            this.period = period;
        }

        public ChatPeriodStatistic(String period) {
            super(null, null, null, null);
            this.period = period;
        }
    }
}
