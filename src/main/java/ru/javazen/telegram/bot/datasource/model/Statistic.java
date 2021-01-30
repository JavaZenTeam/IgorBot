package ru.javazen.telegram.bot.datasource.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.javazen.telegram.bot.model.ChatEntity;
import ru.javazen.telegram.bot.model.UserEntity;

public interface Statistic<T> {
    T getSubject();

    Long getCount();

    Long getLength();

    Double getScore();

    @Getter
    @AllArgsConstructor
    abstract class AbstractStatistic<T> implements Statistic<T> {
        private final T subject;
        private final Long count;
        private final Long length;
        private final Double score;
    }

    class UserStatistic extends AbstractStatistic<UserEntity> {
        public UserStatistic(UserEntity subject, Long count, Long length, Double score) {
            super(subject, count, length, score);
        }
    }

    class ChatStatistic extends AbstractStatistic<ChatEntity> {
        public ChatStatistic(ChatEntity subject, Long count, Long length, Double score) {
            super(subject, count, length, score);
        }
    }
}
