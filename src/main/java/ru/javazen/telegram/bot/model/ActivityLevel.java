package ru.javazen.telegram.bot.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.annotation.Nullable;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ActivityLevel implements LabelSupplier, IdSupplier, Comparable<ActivityLevel> {
    @Id
    private Long id;

    private String name;

    private Integer lowerThreshold;

    @Nullable
    private Integer upperThreshold;

    @Override
    public String getLabel() {
        return name;
    }

    @Override
    public int compareTo(ActivityLevel o) {
        return this.getLowerThreshold().compareTo(o.getLowerThreshold());
    }
}
