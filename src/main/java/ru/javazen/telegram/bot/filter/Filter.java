package ru.javazen.telegram.bot.filter;


import org.telegram.telegrambots.meta.api.objects.Update;

public interface Filter {
    /**
     * @return true for continue process
     */
    boolean check(Update update);

}
