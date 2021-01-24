package ru.javazen.telegram.bot.util;

import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Objects;
import java.util.stream.Stream;

public abstract class MessageHelper {
    public static String getActualText(Message message) {
        return Stream.of(message.getText(), message.getCaption(), message.getNewChatTitle())
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }
}