package ru.javazen.telegram.bot.datasource.model;

import lombok.Getter;

import java.util.Collection;
import java.util.Collections;

@Getter
public class PeriodIdMessageStatistic<T> extends MessageStatistic<T> {
    private final Integer periodId;

    public PeriodIdMessageStatistic(Integer periodId) {
        this.periodId = periodId;
    }

    public PeriodIdMessageStatistic(Integer periodId, T subject, long count, long length, double score) {
        this(periodId, subject, count, length, score, Collections.emptySet());
    }

    public PeriodIdMessageStatistic(Integer periodId, T subject, long count, long length, double score, Collection<? extends MessageStatistic<?>> dataset) {
        super(subject, count, length, score, dataset);
        this.periodId = periodId;
    }
}
