package ru.javazen.telegram.bot.datasource.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChartData {
    private int xKey;
    private int[] yKeys;
    private Object[][] data;
    private String[] labels;
}
