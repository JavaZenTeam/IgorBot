package ru.javazen.telegram.bot.handler;

import org.springframework.beans.factory.annotation.Autowired;
import ru.javazen.telegram.bot.entity.request.Update;
import ru.javazen.telegram.bot.service.MessageHelper;
import ru.javazen.telegram.bot.service.TelegramService;

public class UpdateInfoProvider implements UpdateHandler {

    @Autowired
    private TelegramService telegramService;

    @Override
    public boolean handle(Update update, String token) {

        telegramService.execute(MessageHelper.answer(update.getMessage(), update.toString()), token);

        return true;
    }
}
