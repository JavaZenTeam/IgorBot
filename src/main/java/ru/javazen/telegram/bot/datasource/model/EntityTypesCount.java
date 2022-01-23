package ru.javazen.telegram.bot.datasource.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class EntityTypesCount {
    private final Long chatCount;
    private final Long userCount;
}
