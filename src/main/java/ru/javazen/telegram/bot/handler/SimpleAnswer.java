package ru.javazen.telegram.bot.handler;

import org.springframework.util.Assert;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ru.javazen.telegram.bot.handler.base.MessageHandler;

public class SimpleAnswer implements MessageHandler {

    private String answer;

    public SimpleAnswer(String answer) {
        Assert.notNull(answer, "answer can not be null");
        this.answer = answer;
    }

    @Override
    public boolean handle(Message message, AbsSender sender) throws TelegramApiException {
        sender.execute(new SendMessage(message.getChatId(), answer));
        return true;
    }
}
