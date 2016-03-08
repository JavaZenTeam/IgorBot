package ru.javazen.telegram.bot.handler;

import ru.javazen.telegram.bot.entity.request.Update;
import ru.javazen.telegram.bot.entity.response.SendSticker;
import ru.javazen.telegram.bot.method.SendStickerMethod;
import ru.javazen.telegram.bot.method.TelegramMethod;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GetStickerById implements UpdateHandler {
    private static final Pattern DEFAULT_PATTERN = Pattern.compile("/sticker (.+)");
    private Pattern pattern = DEFAULT_PATTERN;

    public TelegramMethod handle(Update update) {
        String text = update.getMessage().getText();
        if (text == null) return null;
        Matcher matcher = pattern.matcher(text);
        if (!matcher.matches() || matcher.groupCount() < 1) return null;

        SendSticker sendSticker = new SendSticker();
        sendSticker.setChatId(update.getMessage().getChat().getId());
        sendSticker.setSticker(matcher.group(1));
        return new SendStickerMethod(sendSticker);
    }

    public void setPattern(String pattern) {
        this.pattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
    }
}
