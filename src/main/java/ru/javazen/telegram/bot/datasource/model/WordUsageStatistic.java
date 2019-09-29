package ru.javazen.telegram.bot.datasource.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigInteger;

@Setter
@Getter
@ToString
@AllArgsConstructor
public class WordUsageStatistic {
    private String word;
    private BigInteger count;
    private Double delta;
}
