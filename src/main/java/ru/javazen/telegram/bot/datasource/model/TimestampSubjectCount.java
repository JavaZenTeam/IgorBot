package ru.javazen.telegram.bot.datasource.model;

import lombok.Getter;

import java.sql.Timestamp;

@Getter
public class TimestampSubjectCount<T> extends SubjectCount<T> {
    private final Timestamp timestamp;

    public TimestampSubjectCount(Timestamp timestamp, T subject, Long count) {
        super(subject, count);
        this.timestamp = timestamp;
    }

    public TimestampSubjectCount(Timestamp timestamp) {
        super(null, 0);
        this.timestamp = timestamp;
    }
}
