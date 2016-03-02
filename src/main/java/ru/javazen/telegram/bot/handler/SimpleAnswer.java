package ru.javazen.telegram.bot.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import ru.javazen.telegram.bot.entity.request.Update;
import ru.javazen.telegram.bot.service.MessageHelper;
import ru.javazen.telegram.bot.service.TelegramService;

public class SimpleAnswer implements UpdateHandler{
    private String answer;

    @Autowired
    private TelegramService telegramService;

    public SimpleAnswer(String answer) {
        Assert.notNull(answer, "answer can not be null");
        this.answer = answer;
    }

    @Override
    public boolean handle(Update update, String token) {
        telegramService.execute(MessageHelper.answer(update.getMessage(), answer), token);
        return true;
    }
}
