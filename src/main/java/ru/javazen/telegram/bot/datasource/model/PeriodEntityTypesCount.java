package ru.javazen.telegram.bot.datasource.model;

import lombok.Getter;

import java.sql.Timestamp;

@Getter
public class PeriodEntityTypesCount extends EntityTypesCount {
    private final Timestamp period;

    public PeriodEntityTypesCount(Timestamp period, Long chatCount, Long userCount) {
        super(chatCount, userCount);
        this.period = period;
    }
}
