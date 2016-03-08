package ru.javazen.telegram.bot;

import java.util.Comparator;

public class HashCodeComparator implements Comparator<String> {
    @Override
    public int compare(String o1, String o2) {
        return Integer.compare(o1.toLowerCase().hashCode(), o2.toLowerCase().hashCode());
    }
}
