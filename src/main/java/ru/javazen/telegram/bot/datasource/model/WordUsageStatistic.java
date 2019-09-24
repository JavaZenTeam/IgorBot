package ru.javazen.telegram.bot.datasource.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Setter
@Getter
@ToString
@AllArgsConstructor
public class WordUsageStatistic {
    private String word;
    private BigDecimal global;
    private BigDecimal local;
    private BigDecimal delta;
}
