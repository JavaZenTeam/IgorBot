package ru.javazen.telegram.bot.util;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;

public abstract class MessageHelper {

    public static SendMessage answer(Message message, String text, boolean reply){
        SendMessage sendMessage = new SendMessage()
        .setChatId(message.getChat().getId())
        .setText(text);
        if (reply){
            sendMessage.setReplyToMessageId(message.getMessageId());
        }
        return sendMessage;
    }

    public static SendMessage answer(Message message, String text){
        return answer(message, text, false);
    }

    public static String getActualText(Message message){
        return message.getText() != null ?
                message.getText() :
                message.getCaption();
    }
}