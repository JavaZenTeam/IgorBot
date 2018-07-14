package ru.javazen.telegram.bot.handler;

import org.springframework.util.Assert;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ru.javazen.telegram.bot.handling.annotation.Handling;
import ru.javazen.telegram.bot.handling.annotation.UpdateType;
import ru.javazen.telegram.bot.util.MessageHelper;

@Handling(UpdateType.MESSAGE)
public class SimpleAnswer implements UpdateHandler{

    private String answer;

    public SimpleAnswer(String answer) {
        Assert.notNull(answer, "answer can not be null");
        this.answer = answer;
    }

    @Override
    public boolean handle(Update update, AbsSender sender) throws TelegramApiException {
        sender.execute(MessageHelper.answer(update.getMessage(), answer));
        return true;
    }
}
