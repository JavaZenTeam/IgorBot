package ru.javazen.telegram.bot.datasource.model;

import lombok.Getter;
import lombok.Setter;
import ru.javazen.telegram.bot.model.UserEntity;

@Getter
@Setter
public class PeriodUserStatistic extends UserStatistic {
    private String period;

    public PeriodUserStatistic(String period, UserEntity user, Long count, Long length, Double score) {
        super(user, count, length, score);
        this.period = period;
    }

    public PeriodUserStatistic(String period) {
        super(null, null, null, null);
        this.period = period;
    }
}
