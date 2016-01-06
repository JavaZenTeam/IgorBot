package ru.javazen.telegram.bot.handler;

import ru.javazen.telegram.bot.entity.SendMessage;
import ru.javazen.telegram.bot.entity.Update;
import ru.javazen.telegram.bot.method.SendMessageMethod;
import ru.javazen.telegram.bot.method.TelegramMethod;

public class Repeater implements UpdateHandler {
    public TelegramMethod handle(Update update) {
        SendMessage message = new SendMessage();
        message.setChatId(update.getMessage().getChat().getId());
        message.setText(update.getMessage().getText());

        return new SendMessageMethod(message);
    }
}
