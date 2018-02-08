package ru.javazen.telegram.bot.handler;

import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;
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
    public boolean handle(Update update, AbsSender sender)throws TelegramApiException {
        return filters.stream().allMatch(f -> f != null && f.check(update)) &&
                handlers.stream().anyMatch(h -> {
                    try {
                        return h.handle(update, sender);
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                });
    }
}
