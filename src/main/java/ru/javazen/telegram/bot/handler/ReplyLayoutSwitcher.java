package ru.javazen.telegram.bot.handler;

import ru.javazen.telegram.bot.entity.request.Message;
import ru.javazen.telegram.bot.entity.request.Update;
import ru.javazen.telegram.bot.method.TelegramMethod;
import ru.javazen.telegram.bot.service.MessageHelper;

public class ReplyLayoutSwitcher implements UpdateHandler {

    private final static char[] RUS_CHARS = "йцукенгшщзхъфывапролджэячсмитьбю.ёЙЦУКЕНГШЩЗХЪФЫВАПРОЛДЖЭЯЧСМИТЬБЮ,Ё\"№;%:?".toCharArray();
    private final static char[] ENG_CHARS = "qwertyuiop[]asdfghjkl;'zxcvbnm,./`QWERTYUIOP{}ASDFGHJKL:\"ZXCVBNM<>?~@#$%^&".toCharArray();

    public TelegramMethod handle(Update update) {
        String text = update.getMessage().getText();
        Message replyMessage = update.getMessage().getReplyMessage();
        if (text == null || replyMessage == null || replyMessage.getText() == null) return null;

        String result = switchLayout(replyMessage.getText());
        return MessageHelper.answerWithReply(replyMessage, result);
    }

    private static String switchLayout(String text) {
        for (int i=0; i<RUS_CHARS.length && i<ENG_CHARS.length; i++) {
            text = text.replace(ENG_CHARS[i], RUS_CHARS[i]);
        }
        return text;
    }

}
