package ru.javazen.telegram.bot.handler;

import ru.javazen.telegram.bot.comparator.ContextWrapperComparator;
import ru.javazen.telegram.bot.entity.request.Update;

import java.util.Comparator;
import java.util.regex.Matcher;

public class ChoiceMakerWithContext extends ChoiceMaker {
    private static final String CONTEXT_GROUP_NAME = "context";
    private ContextWrapperComparator wrapperComparator = new ContextWrapperComparator();

    @Override
    protected void parseParameters(Update update, Matcher matcher) {
        super.parseParameters(update, matcher);
        String context = matcher.group(CONTEXT_GROUP_NAME);
        wrapperComparator.setContext(process(context, update));
    }

    @Override
    public void setComparator(Comparator<String> comparator) {
        wrapperComparator = new ContextWrapperComparator(comparator);
        super.setComparator(comparator);
    }

    @Override
    public Comparator<String> getComparator() {
        return wrapperComparator.getContext() != null ?
                wrapperComparator :
                super.getComparator();
    }


}
