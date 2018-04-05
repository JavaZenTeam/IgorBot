package ru.javazen.telegram.bot.container;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

@Component
@Scope("prototype")
public class SizedItemsContainerTreeMapImpl<T> implements SizedItemsContainer<T> {
    private TreeMap<Double, T> treeMap = new TreeMap<>();

    @Override
    public void put(T item, Double size) {
        if (size <= 0) throw new IllegalArgumentException("size should be positive");
        treeMap.put(size + size(), item);
    }

    @Override
    public T get(Double index) {
        return Optional
                .ofNullable(treeMap.higherEntry(index))
                .map(Map.Entry::getValue)
                .orElse(null);
    }

    @Override
    public Double size() {
        return treeMap.isEmpty() ? 0.0 : treeMap.lastKey();
    }
}
