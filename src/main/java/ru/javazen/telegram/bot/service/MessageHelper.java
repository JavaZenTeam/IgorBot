package ru.javazen.telegram.bot.service;

import ru.javazen.telegram.bot.entity.request.Message;
import ru.javazen.telegram.bot.entity.response.SendMessage;
import ru.javazen.telegram.bot.method.SendMessageMethod;

public abstract class MessageHelper {
    public static SendMessageMethod answer(Message message, String text){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChat().getId());
        sendMessage.setText(text);
        return new SendMessageMethod(sendMessage);
    }

    public static SendMessageMethod answerWithReply(Message message, String text){
        SendMessageMethod sendMessage = answer(message, text);
        sendMessage.getEntity().setReplyMessageId(message.getMessageId());
        return sendMessage;
    }
}
