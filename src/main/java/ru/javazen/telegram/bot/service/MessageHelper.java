package ru.javazen.telegram.bot.service;

import ru.javazen.telegram.bot.entity.request.Message;
import ru.javazen.telegram.bot.entity.response.SendMessage;

public abstract class MessageHelper {
    public static SendMessage answer(Message message, String text, boolean reply){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChat().getId());
        sendMessage.setText(text);
        if (reply){
            sendMessage.setReplyMessageId(message.getMessageId());
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
