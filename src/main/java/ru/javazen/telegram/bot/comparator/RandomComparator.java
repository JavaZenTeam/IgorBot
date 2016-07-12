package ru.javazen.telegram.bot.comparator;

import java.util.Comparator;
import java.util.Random;

public class RandomComparator implements Comparator<String> {
    private static final Random RANDOM = new Random();

    @Override
    public int compare(String o1, String o2) {
        RANDOM.setSeed(o1.toLowerCase().hashCode());
        int i1 = RANDOM.nextInt();

        RANDOM.setSeed(o2.toLowerCase().hashCode());
        int i2 = RANDOM.nextInt();

        return Integer.compare(i1, i2);
    }
}
