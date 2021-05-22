package ru.javazen.telegram.bot.datasource.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Collection;
import java.util.Collections;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public class Statistic<T> {
    private final T subject;
    private final long count;
    private final long length;
    private final double score;

    @Setter
    private Collection<? extends Statistic<?>> dataset = Collections.emptySet();

    public Statistic() {
        this(null, 0, 0, 0);
    }

    public Statistic(T subject, Long count) {
        this(subject, count, 0, 0);
    }

    public long getScorePercentage() {
        return Math.round(100 * getScoreRatio());
    }

    public double getScoreRatio() {
        double totalScore = getDataset().stream().mapToDouble(Statistic::getScore).sum();
        return getScore() / totalScore;
    }
}
