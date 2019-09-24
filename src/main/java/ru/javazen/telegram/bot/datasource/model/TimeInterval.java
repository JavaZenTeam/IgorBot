package ru.javazen.telegram.bot.datasource.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TimeInterval {
    private int interval;
    private Unit unit;

    @AllArgsConstructor
    @Getter
    public enum Unit {
        YEAR("YYYY"),
        MONTH("YYYY-MM"),
        DAY("YYYY-MM-dd"),
        HOUR("YYYY-MM-dd HH:mm");
        private String datetimeFormat;
    }

}
