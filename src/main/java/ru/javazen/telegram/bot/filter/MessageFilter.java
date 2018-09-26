package ru.javazen.telegram.bot.filter;

import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;

public interface MessageFilter extends Filter {
    @Override
    default boolean check(Update update) {
        return update.getMessage() != null && check(update.getMessage());
    }

    boolean check(Message message);
}
