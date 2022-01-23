package ru.javazen.telegram.bot.datasource.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

import java.util.List;

@Getter
@Builder
public class ChartData {
    private final Object[][] data;
    @Singular
    private final List<String> labels;
    @Singular
    private final List<Long> ids;
}
