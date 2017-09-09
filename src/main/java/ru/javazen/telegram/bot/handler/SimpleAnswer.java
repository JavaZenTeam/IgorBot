package ru.javazen.telegram.bot.handler;

import org.springframework.util.Assert;
import ru.javazen.telegram.bot.BotMethodExecutor;
import ru.javazen.telegram.bot.entity.Update;
import ru.javazen.telegram.bot.util.MessageHelper;

public class SimpleAnswer implements UpdateHandler{

    private String answer;

    public SimpleAnswer(String answer) {
        Assert.notNull(answer, "answer can not be null");
        this.answer = answer;
    }

    @Override
    public boolean handle(Update update, BotMethodExecutor executor) {
        executor.execute(MessageHelper.answer(update.getMessage(), answer), Void.class);
        return true;
    }
}
