package ru.javazen.telegram.bot.datasource.model;

import lombok.Getter;

import java.sql.Timestamp;

@Getter
public class PeriodMessageStatistic<T> extends MessageStatistic<T> {
    private final Timestamp period;

    public PeriodMessageStatistic(Timestamp period, T subject, Long count, Long length, Double score) {
        super(subject, count, length, score);
        this.period = period;
    }

    public PeriodMessageStatistic(Timestamp period) {
        this.period = period;
    }
}
