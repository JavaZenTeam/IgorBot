package ru.javazen.telegram.bot.datasource.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CountStatistic {
    private final String key;
    private final Long count;
}
