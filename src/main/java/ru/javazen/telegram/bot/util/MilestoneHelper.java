package ru.javazen.telegram.bot.util;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class MilestoneHelper {
    public static final long MIN_MILESTONE = 1000;
    public static final long STEP = 10;

    public boolean isMilestone(long number) {
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

    public long prevMilestone(long number) {
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

    public long nextMilestone(long number) {
        if (number < MIN_MILESTONE) {
            return MIN_MILESTONE;
        }
        if (number / MIN_MILESTONE < STEP) {
            return (1 + number / MIN_MILESTONE) * MIN_MILESTONE;
        }
        return STEP * nextMilestone(number / STEP);
    }

    public MilestoneSummary getMilestoneSummary(Long prevPosition, Long currentPosition) {
        return new MilestoneSummary(
                prevMilestone(prevPosition),
                prevPosition,
                currentPosition,
                nextMilestone(currentPosition)
        );
    }

    @AllArgsConstructor
    public static class MilestoneSummary {
        public final long prevMilestone;
        public final long prevPosition;
        public final long currPosition;
        public final long nextMilestone;
    }
}
