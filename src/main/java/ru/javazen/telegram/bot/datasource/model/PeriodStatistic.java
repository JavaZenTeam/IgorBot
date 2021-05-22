package ru.javazen.telegram.bot.datasource.model;

import lombok.Getter;

import java.sql.Timestamp;

@Getter
public class PeriodStatistic<T> extends Statistic<T> {
    private final Timestamp period;

    public PeriodStatistic(Timestamp period, T subject, Long count, Long length, Double score) {
        super(subject, count, length, score);
        this.period = period;
    }

    public PeriodStatistic(Timestamp period, T subject, Long count) {
        super(subject, count);
        this.period = period;
    }

    public PeriodStatistic(Timestamp period) {
        this.period = period;
    }
}
