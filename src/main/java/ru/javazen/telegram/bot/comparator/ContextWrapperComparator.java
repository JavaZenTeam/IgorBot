package ru.javazen.telegram.bot.comparator;

import java.util.Comparator;

public class ContextWrapperComparator implements Comparator<String>{
    private Comparator<String> innerComparator;
    private String context;

    public ContextWrapperComparator(Comparator<String> innerComparator) {
        this.innerComparator = innerComparator;
    }

    public ContextWrapperComparator() {
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getContext() {
        return context;
    }

    @Override
    public int compare(String o1, String o2) {
        return innerComparator.compare(o1 + context, o2 + context);
    }
}
