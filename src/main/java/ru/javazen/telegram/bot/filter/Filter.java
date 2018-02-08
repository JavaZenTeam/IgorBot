package ru.javazen.telegram.bot.filter;


import org.telegram.telegrambots.api.objects.Update;

public interface Filter {
    /**
     * @return true for continue process
     */
    boolean check(Update update);

}
