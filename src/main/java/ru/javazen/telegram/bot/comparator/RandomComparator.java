package ru.javazen.telegram.bot.comparator;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.Comparator;
import java.util.Random;

public class RandomComparator implements Comparator<String> {
    @Autowired
    private Random random;

    @Override
    public int compare(String o1, String o2) {
        random.setSeed(o1.toLowerCase().hashCode());
        int i1 = random.nextInt();

        random.setSeed(o2.toLowerCase().hashCode());
        int i2 = random.nextInt();

        return Integer.compare(i1, i2);
    }
}
