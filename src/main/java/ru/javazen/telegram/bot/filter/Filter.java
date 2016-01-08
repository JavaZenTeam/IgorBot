package ru.javazen.telegram.bot.filter;

import ru.javazen.telegram.bot.entity.request.Update;

public interface Filter {
    /**
     * @return true for continue process
     */
    boolean check(Update update);

}
