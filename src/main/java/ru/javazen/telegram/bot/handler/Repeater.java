package ru.javazen.telegram.bot.handler;

import org.springframework.beans.factory.annotation.Autowired;
import ru.javazen.telegram.bot.Bot;
import ru.javazen.telegram.bot.entity.request.Update;
import ru.javazen.telegram.bot.service.MessageHelper;
import ru.javazen.telegram.bot.service.TelegramBotService;

public class Repeater implements UpdateHandler {

    @Autowired
    private TelegramBotService botService;

    @Override
    public boolean handle(Update update) {
        String text = update.getMessage().getText();
        if (text == null) return false;
        botService.sendMessage(MessageHelper.answer(update.getMessage(), text));
        return true;
    }
}
