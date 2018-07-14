package ru.javazen.telegram.bot.handler;

import org.telegram.telegrambots.api.methods.send.SendSticker;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ru.javazen.telegram.bot.handling.annotation.Handling;
import ru.javazen.telegram.bot.handling.annotation.UpdateType;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Handling(UpdateType.MESSAGE)
public class GetStickerById implements UpdateHandler {
    private static final Pattern DEFAULT_PATTERN = Pattern.compile("/sticker (.+)");
    private Pattern pattern = DEFAULT_PATTERN;

    @Override
    public boolean handle(Update update, AbsSender sender) throws TelegramApiException {
        String text = update.getMessage().getText();
        if (text == null) return false;
        Matcher matcher = pattern.matcher(text);
        if (!matcher.matches() || matcher.groupCount() < 1) return false;

        SendSticker sendSticker = new SendSticker();
        sendSticker.setChatId(update.getMessage().getChat().getId());
        sendSticker.setSticker(matcher.group(1));

        sender.sendSticker(sendSticker);

        return true;
    }

    public void setPattern(String pattern) {
        this.pattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
    }
}
