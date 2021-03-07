package ru.javazen.telegram.bot.datasource.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ru.javazen.telegram.bot.model.ChatEntity;
import ru.javazen.telegram.bot.model.UserEntity;

import java.util.Collection;
import java.util.Collections;

public interface Statistic<T> {
    T getSubject();

    Long getCount();

    Long getLength();

    Double getScore();

    Double getScoreRatio();

    default long getScorePercentage() {
        return Math.round(100 * getScoreRatio());
    }

    @Getter
    @RequiredArgsConstructor
    abstract class AbstractStatistic<T> implements Statistic<T> {
        private final T subject;
        private final Long count;
        private final Long length;
        private final Double score;

        @Setter
        private Collection<? extends Statistic<?>> dataset = Collections.emptySet();

        @Override
        public Double getScoreRatio() {
            double totalScore = getDataset().stream().mapToDouble(Statistic::getScore).sum();
            return getScore() / totalScore;
        }
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

    class StringStatistic extends AbstractStatistic<String> {
        public StringStatistic(String subject, Long count, Long length, Double score, Collection<? extends Statistic<?>> dataSet) {
            super(subject, count, length, score);
            setDataset(dataSet);
        }
    }
}
