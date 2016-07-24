package ru.javazen.telegram.bot.handler;

import org.springframework.util.Assert;
import ru.javazen.telegram.bot.Bot;
import ru.javazen.telegram.bot.entity.request.Update;
import ru.javazen.telegram.bot.service.MessageHelper;

public class SimpleAnswer implements UpdateHandler{
    private String answer;

    public SimpleAnswer(String answer) {
        Assert.notNull(answer, "answer can not be null");
        this.answer = answer;
    }

    @Override
    public boolean handle(Update update, Bot bot) {
        bot.getService().sendMessage(MessageHelper.answer(update.getMessage(), answer));
        return true;
    }
}
