package ru.javazen.telegram.bot.handler;

import org.springframework.beans.factory.BeanNameAware;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.javazen.telegram.bot.filter.Filter;
import ru.javazen.telegram.bot.handler.base.UpdateHandler;

import java.util.Collections;
import java.util.List;

public class FilterAdapter implements UpdateHandler, BeanNameAware {
    private final List<Filter> filters;
    private final List<UpdateHandler> handlers;
    private String beanName;

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
    public boolean handle(Update update, AbsSender sender) throws TelegramApiException {
        for (Filter filter : filters) {
            if (!filter.check(update)) {
                return false;
            }
        }
        for (UpdateHandler handler : handlers) {
            if (handler.handle(update, sender)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    @Override
    public String getName() {
        return beanName;
    }
}
