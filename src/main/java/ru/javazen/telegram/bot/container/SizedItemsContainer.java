package ru.javazen.telegram.bot.container;

public interface SizedItemsContainer<T> {
    void put(T item, Double size);
    T get(Double x);
    Double size();
}
