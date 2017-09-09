package ru.javazen.telegram.bot.handler;

import ru.javazen.telegram.bot.BotMethodExecutor;
import ru.javazen.telegram.bot.filter.Filter;

import java.util.Collections;
import java.util.List;

public class FilterAdapter implements UpdateHandler {
    private List<Filter> filters;
    private List<UpdateHandler> handlers;

    public FilterAdapter(Filter filter, UpdateHandler handler) {
        this(Collections.singletonList(filter), Collections.singletonList(handler));
    }

    public FilterAdapter(List<Filter> filters, UpdateHandler handler) {
        this(filters, Collections.singletonList(handler));
    }

    public FilterAdapter(Filter filter, List<UpdateHandler> handlers) {
        this(Collections.singletonList(filter), handlers);
    }

    public FilterAdapter(List<Filter> filters, List<UpdateHandler> handlers) {
        this.filters = filters;
        this.handlers = handlers;
    }

    @Override
    public boolean handle(ru.javazen.telegram.bot.entity.Update update, BotMethodExecutor executor) {
        return filters.stream().allMatch(f -> f != null && f.check(update)) &&
                handlers.stream().anyMatch(h -> h.handle(update, executor));
    }
}
