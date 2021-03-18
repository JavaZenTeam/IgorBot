package ru.javazen.telegram.bot.datasource.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChartData {
    private Object[][] data;
    private String[] labels;
    private String[] ids;
}
