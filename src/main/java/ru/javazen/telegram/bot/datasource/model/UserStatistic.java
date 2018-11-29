package ru.javazen.telegram.bot.datasource.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.javazen.telegram.bot.model.UserEntity;

@Getter
@AllArgsConstructor
public class UserStatistic {
    private final UserEntity user;
    private final Long count;
    private final Long length;
    private final Double score;
}
