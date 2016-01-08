package ru.javazen.telegram.bot.handler;

import org.springframework.util.Assert;
import ru.javazen.telegram.bot.entity.request.Update;
import ru.javazen.telegram.bot.method.TelegramMethod;
import ru.javazen.telegram.bot.service.MessageHelper;

public class SimpleAnswer implements UpdateHandler{
    private String answer;

    public SimpleAnswer(String answer) {
        Assert.notNull(answer, "answer can not be null");
        this.answer = answer;
    }

    @Override
    public TelegramMethod handle(Update update) {
        return MessageHelper.answer(update.getMessage(), answer);
    }
}
