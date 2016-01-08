package ru.javazen.telegram.bot.handler;

import org.springframework.util.Assert;
import ru.javazen.telegram.bot.entity.request.Update;
import ru.javazen.telegram.bot.entity.response.SendMessage;
import ru.javazen.telegram.bot.method.SendMessageMethod;
import ru.javazen.telegram.bot.method.TelegramMethod;

public class SimpleAnswer implements UpdateHandler{
    private String answer;

    public SimpleAnswer(String answer) {
        Assert.notNull(answer, "answer can not be null");
        this.answer = answer;
    }

    @Override
    public TelegramMethod handle(Update update) {
        SendMessage message = new SendMessage();
        message.setChatId(update.getMessage().getChat().getId());
        message.setText(answer);
        return new SendMessageMethod(message);
    }
}
