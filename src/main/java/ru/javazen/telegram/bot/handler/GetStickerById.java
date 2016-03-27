package ru.javazen.telegram.bot.handler;

import org.springframework.beans.factory.annotation.Autowired;
import ru.javazen.telegram.bot.entity.request.Update;
import ru.javazen.telegram.bot.entity.response.SendSticker;
import ru.javazen.telegram.bot.method.SendStickerMethod;
import ru.javazen.telegram.bot.method.TelegramMethod;
import ru.javazen.telegram.bot.service.TelegramService;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GetStickerById implements UpdateHandler {
    private static final Pattern DEFAULT_PATTERN = Pattern.compile("/sticker (.+)");
    private Pattern pattern = DEFAULT_PATTERN;

    @Autowired
    private TelegramService telegramService;

    @Override
    public boolean handle(Update update, String token) {
        String text = update.getMessage().getText();
        if (text == null) return false;
        Matcher matcher = pattern.matcher(text);
        if (!matcher.matches() || matcher.groupCount() < 1) return false;

        SendSticker sendSticker = new SendSticker();
        sendSticker.setChatId(update.getMessage().getChat().getId());
        sendSticker.setSticker(matcher.group(1));

        telegramService.execute(new SendStickerMethod(sendSticker), token);

        return true;
    }

    public void setPattern(String pattern) {
        this.pattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
    }
}
