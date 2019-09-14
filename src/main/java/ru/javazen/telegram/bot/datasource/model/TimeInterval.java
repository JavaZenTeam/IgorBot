package ru.javazen.telegram.bot.datasource.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TimeInterval {
    private int interval;
    private Unit unit;

    public enum Unit {
        YEAR,
        MONTH,
        DAY,
        HOUR,
    }

}
