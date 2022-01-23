package ru.javazen.telegram.bot.datasource.model;

import lombok.Getter;

import java.sql.Timestamp;

@Getter
public class TimestampMessageStatistic<T> extends MessageStatistic<T> {
    private final Timestamp timestamp;

    public TimestampMessageStatistic(Timestamp timestamp, T subject, Long count, Long length, Double score) {
        super(subject, count, length, score);
        this.timestamp = timestamp;
    }

    public TimestampMessageStatistic(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
