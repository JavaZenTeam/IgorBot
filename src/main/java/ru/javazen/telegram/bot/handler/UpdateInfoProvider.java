package ru.javazen.telegram.bot.handler;

import ru.javazen.telegram.bot.Bot;
import ru.javazen.telegram.bot.entity.request.Update;
import ru.javazen.telegram.bot.service.MessageHelper;

public class UpdateInfoProvider implements UpdateHandler {

    @Override
    public boolean handle(Update update, Bot bot) {

        bot.getService().sendMessage(MessageHelper.answer(update.getMessage(), update.toString()));

        return true;
    }
}
