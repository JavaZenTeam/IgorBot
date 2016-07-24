package ru.javazen.telegram.bot.handler;

import ru.javazen.telegram.bot.Bot;
import ru.javazen.telegram.bot.entity.request.Update;
import ru.javazen.telegram.bot.entity.response.SendSticker;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GetStickerById implements UpdateHandler {
    private static final Pattern DEFAULT_PATTERN = Pattern.compile("/sticker (.+)");
    private Pattern pattern = DEFAULT_PATTERN;

    @Override
    public boolean handle(Update update, Bot bot) {
        String text = update.getMessage().getText();
        if (text == null) return false;
        Matcher matcher = pattern.matcher(text);
        if (!matcher.matches() || matcher.groupCount() < 1) return false;

        SendSticker sendSticker = new SendSticker();
        sendSticker.setChatId(update.getMessage().getChat().getId());
        sendSticker.setSticker(matcher.group(1));

        bot.getService().sendSticker(sendSticker);

        return true;
    }

    public void setPattern(String pattern) {
        this.pattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
    }
}
