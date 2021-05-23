package ru.javazen.telegram.bot.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.annotation.Nullable;

@Getter
@AllArgsConstructor
public enum ActivityLevel implements LabelSupplier, IdSupplier, Comparable<ActivityLevel> {
    LOW("Low activity", 0, 10),
    MID("Middle activity", 10, 100),
    HIGH("High activity", 100, null),
    ;

    private final String label;

    private final Integer lowerThreshold;

    @Nullable
    private final Integer upperThreshold;

    @Override
    public Long getId() {
        return 1L + ordinal();
    }
}
