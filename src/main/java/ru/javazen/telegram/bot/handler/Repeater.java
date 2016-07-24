package ru.javazen.telegram.bot.handler;

import ru.javazen.telegram.bot.Bot;
import ru.javazen.telegram.bot.entity.request.Update;
import ru.javazen.telegram.bot.service.MessageHelper;

public class Repeater implements UpdateHandler {

    @Override
    public boolean handle(Update update, Bot bot) {
        String text = update.getMessage().getText();
        if (text == null) return false;
        bot.getService().sendMessage(MessageHelper.answer(update.getMessage(), text));
        return true;
    }
}
