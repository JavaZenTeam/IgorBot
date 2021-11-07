package ru.javazen.telegram.bot.util;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class MilestoneHelper {
    public static final int MIN_MILESTONE = 1000;
    public static final int STEP = 10;

    public boolean isMilestone(int number) {
        if (number < MIN_MILESTONE) {
            return false;
        }
        if (number % MIN_MILESTONE != 0) {
            return false;
        }
        if (number / MIN_MILESTONE < STEP) {
            return true;
        } else {
            return isMilestone(number / STEP);
        }
    }

    public int prevMilestone(int number) {
        if (number <= MIN_MILESTONE) {
            return 0;
        }
        if (number / MIN_MILESTONE == STEP) {
            return (number - 1) / MIN_MILESTONE * MIN_MILESTONE;
        }
        if (number / MIN_MILESTONE < STEP) {
            return number / MIN_MILESTONE * MIN_MILESTONE;
        }
        return STEP * prevMilestone(number / STEP);
    }

    public int nextMilestone(int number) {
        if (number < MIN_MILESTONE) {
            return MIN_MILESTONE;
        }
        if (number / MIN_MILESTONE < STEP) {
            return (1 + number / MIN_MILESTONE) * MIN_MILESTONE;
        }
        return STEP * nextMilestone(number / STEP);
    }

    public MilestoneSummary getMilestoneSummary(int prevPosition, int currentPosition) {
        return new MilestoneSummary(
                prevMilestone(prevPosition),
                prevPosition,
                currentPosition,
                nextMilestone(currentPosition)
        );
    }

    @AllArgsConstructor
    public static class MilestoneSummary {
        public final int prevMilestone;
        public final int prevPosition;
        public final int currPosition;
        public final int nextMilestone;
    }
}
