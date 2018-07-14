package ru.javazen.telegram.bot.handler;

import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ru.javazen.telegram.bot.handling.annotation.Handling;
import ru.javazen.telegram.bot.handling.annotation.UpdateType;
import ru.javazen.telegram.bot.util.MessageHelper;

@Handling(UpdateType.MESSAGE)
public class Repeater implements UpdateHandler {

    @Override
    public boolean handle(Update update, AbsSender sender) throws TelegramApiException {
        String text = update.getMessage().getText();
        if (text == null) return false;

        sender.execute(MessageHelper.answer(update.getMessage(), text));

        return true;
    }
}
