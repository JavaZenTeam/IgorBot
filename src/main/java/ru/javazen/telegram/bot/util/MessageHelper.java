package ru.javazen.telegram.bot.util;

import org.telegram.telegrambots.meta.api.objects.Message;

public abstract class MessageHelper {
    public static String getActualText(Message message){
        return message.getText() != null ? message.getText() : message.getCaption();
    }
}