package ru.javazen.telegram.bot.util;

import lombok.Getter;
import ru.javazen.telegram.bot.datasource.model.MessageStatistic;

import java.util.List;

@Getter
public class ActivityStatisticSummary {
    private final List<? extends MessageStatistic<?>> statisticItems;
    private final Class<?> subjectClass;
    private final int preferredSize;
    private final Long limitValue;
    private final MessageStatistic<?> overLimitStatistic;

    public ActivityStatisticSummary(List<? extends MessageStatistic<?>> statisticItems, int preferredSize) {
        this.statisticItems = statisticItems;
        this.subjectClass = statisticItems.stream()
                .map(MessageStatistic::getSubject)
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
            long[] values = getStatisticItems().stream().mapToLong(MessageStatistic::getScorePercentage).sorted().toArray();
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
    private MessageStatistic<?> sumOverLimitStats() {
        if (getLimitValue() == null) {
            return null;
        }
        return getStatisticItems().stream()
                .filter(item -> item.getScorePercentage() <= limitValue)
                .map(MessageStatistic.class::cast)
                .reduce((t1, t2) -> new MessageStatistic<String>(
                        "Other",
                        t1.getCount() + t2.getCount(),
                        t1.getLength() + t2.getLength(),
                        t1.getScore() + t2.getScore(),
                        t1.getDataset())
                )
                .orElse(null);
    }
}
