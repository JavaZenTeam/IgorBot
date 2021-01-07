package ru.javazen.telegram.bot.datasource.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TimeInterval {
    private final int interval;
    private final Unit unit;

    @AllArgsConstructor
    @Getter
    public enum Unit {
        YEAR("yyyy"),
        MONTH("yyyy-MM"),
        DAY("yyyy-MM-dd"),
        HOUR("yyyy-MM-dd HH:mm");
        private final String datetimeFormat;
    }

}
