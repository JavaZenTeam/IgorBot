package ru.javazen.telegram.bot.datasource.model;

import lombok.Getter;

import java.sql.Timestamp;

@Getter
public class TimestampEntityTypesCount extends EntityTypesCount {
    private final Timestamp timestamp;

    public TimestampEntityTypesCount(Timestamp timestamp, Long chatCount, Long userCount) {
        super(chatCount, userCount);
        this.timestamp = timestamp;
    }
}
