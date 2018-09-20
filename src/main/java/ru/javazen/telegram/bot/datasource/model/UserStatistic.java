package ru.javazen.telegram.bot.datasource.model;

import ru.javazen.telegram.bot.model.UserEntity;

public class UserStatistic {
    private final UserEntity user;
    private final Long count;
    private final Long length;

    public UserStatistic(UserEntity user, Long count, Long length) {
        this.user = user;
        this.count = count;
        this.length = length;
    }

    public UserEntity getUser() {
        return user;
    }

    public Long getCount() {
        return count;
    }

    public Long getLength() {
        return length;
    }
}
