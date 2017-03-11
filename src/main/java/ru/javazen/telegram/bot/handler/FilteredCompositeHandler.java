package ru.javazen.telegram.bot.handler;

import ru.javazen.telegram.bot.entity.request.Update;
import ru.javazen.telegram.bot.filter.Filter;

import java.util.List;

public class FilteredCompositeHandler implements UpdateHandler {

    private Filter filter;
    private List<UpdateHandler> updateHandlers;

    public FilteredCompositeHandler(Filter filter, List<UpdateHandler> updateHandlers) {
        this.filter = filter;
        this.updateHandlers = updateHandlers;
    }

    @Override
    public boolean handle(Update update) {

        if (filter.check(update)) {
            for (UpdateHandler handler : updateHandlers) {
                if (handler.handle(update)) {
                    return true;
                }
            }
        }

        return false;
    }
}