package ru.javazen.telegram.bot.handler;

import org.telegram.telegrambots.meta.api.objects.Message;
import ru.javazen.telegram.bot.util.comparator.ContextWrapperComparator;

import java.util.Comparator;
import java.util.regex.Matcher;

public class ChoiceMakerWithContext extends ChoiceMaker {
    private static final String CONTEXT_GROUP_NAME = "context";
    private ContextWrapperComparator wrapperComparator = new ContextWrapperComparator();

    @Override
    protected void parseParameters(Message message, Matcher matcher) {
        super.parseParameters(message, matcher);
        String context = matcher.group(CONTEXT_GROUP_NAME);
        wrapperComparator.setContext(process(context, message));
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
