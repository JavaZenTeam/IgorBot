package ru.javazen.telegram.bot.datasource.model;

import lombok.Getter;
import ru.javazen.telegram.bot.model.ChatEntity;
import ru.javazen.telegram.bot.model.UserEntity;

import java.time.LocalDateTime;

public interface PeriodStatistic<T> extends Statistic<T> {
    LocalDateTime getPeriod();

    @Getter
    class UserPeriodStatistic extends Statistic.UserStatistic implements PeriodStatistic<UserEntity> {
        private final LocalDateTime period;

        public UserPeriodStatistic(LocalDateTime period, UserEntity subject, Long count, Long length, Double score) {
            super(subject, count, length, score);
            this.period = period;
        }

        public UserPeriodStatistic(LocalDateTime period) {
            super(null, null, null, null);
            this.period = period;
        }
    }

    @Getter
    class ChatPeriodStatistic extends Statistic.ChatStatistic implements PeriodStatistic<ChatEntity> {
        private final LocalDateTime period;

        public ChatPeriodStatistic(LocalDateTime period, ChatEntity subject, Long count, Long length, Double score) {
            super(subject, count, length, score);
            this.period = period;
        }

        public ChatPeriodStatistic(LocalDateTime period) {
            super(null, null, null, null);
            this.period = period;
        }
    }
}
