package ru.javazen.telegram.bot.util;

import org.modelmapper.Condition;
import org.springframework.util.CollectionUtils;

import java.util.Collection;

public class CustomConditions {
    public static <S extends Collection<?>, D> Condition<S, D> isNotEmpty() {
        return ctx -> !CollectionUtils.isEmpty(ctx.getSource());
    }
}
