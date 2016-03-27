package ru.javazen.telegram.bot.handler;

import org.springframework.util.Assert;
import ru.javazen.telegram.bot.entity.request.Update;
import ru.javazen.telegram.bot.filter.Filter;
import ru.javazen.telegram.bot.method.TelegramMethod;

public class FilterAdapter implements UpdateHandler {
    private Filter filter;
    private UpdateHandler handler;

    public FilterAdapter(Filter filter, UpdateHandler handler) {
        Assert.notNull(filter, "filter can not be null");
        Assert.notNull(handler, "handler can not be null");
        this.filter = filter;
        this.handler = handler;
    }

    @Override
    public boolean handle(Update update, String token) {
        if (!filter.check(update)) return false;
        return handler.handle(update, token);
    }
}
