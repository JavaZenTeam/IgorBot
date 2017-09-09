package ru.javazen.telegram.bot.handler;

import ru.javazen.telegram.bot.BotMethodExecutor;
import ru.javazen.telegram.bot.entity.Update;
import ru.javazen.telegram.bot.util.MessageHelper;

public class Repeater implements UpdateHandler {

    @Override
    public boolean handle(Update update, BotMethodExecutor executor) {
        String text = update.getMessage().getText();
        if (text == null) return false;

        executor.execute(MessageHelper.answer(update.getMessage(), text), Void.class);

        return true;
    }
}
