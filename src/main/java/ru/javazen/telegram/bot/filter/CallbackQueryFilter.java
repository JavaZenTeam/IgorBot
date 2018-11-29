package ru.javazen.telegram.bot.filter;

import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.objects.Update;

@RequiredArgsConstructor
public class CallbackQueryFilter implements Filter {

    private final String callbackPrefix;

    @Override
    public boolean check(Update update) {
        return update.hasCallbackQuery() && update.getCallbackQuery().getData().startsWith(callbackPrefix);
    }
}
