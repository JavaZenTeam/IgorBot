package ru.javazen.telegram.bot.datasource.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Collection;
import java.util.Collections;

@Getter
public class MessageStatistic<T> extends BaseCount<T> {
    private final long length;
    private final double score;

    @Setter
    private Collection<? extends MessageStatistic<?>> dataset;

    public MessageStatistic() {
        this(null, 0, 0, 0);
    }

    public MessageStatistic(T subject, long count, long length, double score) {
        this(subject, count, length, score, Collections.emptySet());
    }

    public MessageStatistic(T subject, long count, long length, double score, Collection<? extends MessageStatistic<?>> dataset) {
        super(subject, count);
        this.length = length;
        this.score = score;
        this.dataset = dataset;
    }

    public long getScorePercentage() {
        return Math.round(100 * getScoreRatio());
    }

    public double getScoreRatio() {
        double totalScore = getDataset().stream().mapToDouble(MessageStatistic::getScore).sum();
        return getScore() / totalScore;
    }
}
