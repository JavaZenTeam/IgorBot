package ru.javazen.telegram.bot.datasource.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BaseCount<T> {
    private final T subject;
    private final long count;
}
