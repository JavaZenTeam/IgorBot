package ru.javazen.telegram.bot.datasource.model;

import lombok.Getter;

import java.sql.Timestamp;

@Getter
public class PeriodCount<T> extends SubjectCount<T> {
    private final Timestamp period;

    public PeriodCount(Timestamp period, T subject, Long count) {
        super(subject, count);
        this.period = period;
    }

    public PeriodCount(Timestamp period) {
        super(null, 0);
        this.period = period;
    }
}
