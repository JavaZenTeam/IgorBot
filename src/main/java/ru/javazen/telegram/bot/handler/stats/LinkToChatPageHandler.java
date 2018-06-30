package ru.javazen.telegram.bot.handler.stats;

import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ru.javazen.telegram.bot.handler.UpdateHandler;
import ru.javazen.telegram.bot.util.MessageHelper;

import java.text.MessageFormat;

public class LinkToChatPageHandler implements UpdateHandler {
    private String linkTemplate;

    public LinkToChatPageHandler(String linkTemplate) {
        this.linkTemplate = linkTemplate;
    }

    @Override
    public boolean handle(Update update, AbsSender sender) throws TelegramApiException {
        String link = MessageFormat.format(linkTemplate, update.getMessage().getChatId());
        sender.execute(MessageHelper.answer(update.getMessage(), link));
        return true;
    }
}
