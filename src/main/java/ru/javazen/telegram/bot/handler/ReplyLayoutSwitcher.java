package ru.javazen.telegram.bot.handler;

import org.springframework.stereotype.Component;
import ru.javazen.telegram.bot.entity.request.Update;
import ru.javazen.telegram.bot.entity.response.SendMessage;
import ru.javazen.telegram.bot.method.SendMessageMethod;
import ru.javazen.telegram.bot.method.TelegramMethod;

@Component
public class ReplyLayoutSwitcher  implements UpdateHandler {

    private final static char[] RUS_CHARS = "йцукенгшщзхъфывапролджэячсмитьбю.ёЙЦУКЕНГШЩЗХЪФЫВАПРОЛДЖЭЯЧСМИТЬБЮ,Ё\"№;%:?".toCharArray();
    private final static char[] ENG_CHARS = "qwertyuiop[]asdfghjkl;'zxcvbnm,./`QWERTYUIOP{}ASDFGHJKL:\"ZXCVBNM<>?~@#$%^&".toCharArray();

    private String command = "/switch";

    public TelegramMethod handle(Update update) {
        String text = update.getMessage().getText();
        if (text == null) return null;

        if (!command.equalsIgnoreCase(text.trim())) return null;

        SendMessage message = new SendMessage();

        message.setChatId(update.getMessage().getChat().getId());
        message.setReplyMessageId(update.getMessage().getReplyMessage().getMessageId());
        message.setText(switchLayout(update.getMessage().getReplyMessage().getText()));

        return new SendMessageMethod(message);
    }

    private static String switchLayout(String text) {
        if (text == null) return null;

        for (int i=0; i<RUS_CHARS.length && i<ENG_CHARS.length; i++) {
            text = text.replace(ENG_CHARS[i], RUS_CHARS[i]);
        }
        return text;
    }

}
