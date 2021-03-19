package ru.javazen.telegram.bot.util;

import lombok.Getter;
import ru.javazen.telegram.bot.datasource.model.Statistic;

import java.util.List;

@Getter
public class ActivityStatisticSummary {
    private final List<? extends Statistic<?>> statisticItems;
    private final Class<?> subjectClass;
    private final int preferredSize;
    private final Long limitValue;
    private final Statistic.AbstractStatistic<?> overLimitStatistic;

    public ActivityStatisticSummary(List<? extends Statistic<?>> statisticItems, int preferredSize) {
        this.statisticItems = statisticItems;
        this.subjectClass = statisticItems.stream()
                .map(Statistic::getSubject)
                .map(Object::getClass)
                .map(Class.class::cast)
                .findAny()
                .orElse(Object.class);
        this.preferredSize = preferredSize;
        this.limitValue = calcLimitValue();
        this.overLimitStatistic = sumOverLimitStats();
    }

    public boolean isEmpty() {
        return statisticItems.isEmpty();
    }

    public boolean isLimited() {
        return limitValue != null;
    }

    private Long calcLimitValue() {
        if (getStatisticItems().size() > getPreferredSize()) {
            long[] values = getStatisticItems().stream().mapToLong(Statistic::getScorePercentage).sorted().toArray();
            long scoreLimit = values[getStatisticItems().size() - getPreferredSize()];
            long filtered = getStatisticItems().stream()
                    .filter(item -> item.getScorePercentage() <= scoreLimit)
                    .count();
            if (filtered < 3) {
                return null;
            } else {
                return scoreLimit;
            }
        } else {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private Statistic.AbstractStatistic<?> sumOverLimitStats() {
        if (getLimitValue() == null) {
            return null;
        }
        return getStatisticItems().stream()
                .filter(item -> item.getScorePercentage() <= limitValue)
                .map(Statistic.AbstractStatistic.class::cast)
                .reduce((t1, t2) -> new Statistic.StringStatistic(
                        "Other",
                        t1.getCount() + t2.getCount(),
                        t1.getLength() + t2.getLength(),
                        t1.getScore() + t2.getScore(),
                        t1.getDataset())
                )
                .orElse(null);
    }
}
