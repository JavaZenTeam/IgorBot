package ru.javazen.telegram.bot.handler;

import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.javazen.telegram.bot.handler.base.TextMessageHandler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GetStickerById implements TextMessageHandler {
    private static final Pattern DEFAULT_PATTERN = Pattern.compile("/sticker (.+)");
    private Pattern pattern = DEFAULT_PATTERN;

    @Override
    public boolean handle(Message message, String text, AbsSender sender) throws TelegramApiException {
        Matcher matcher = pattern.matcher(text);
        if (!matcher.matches() || matcher.groupCount() < 1) return false;

        SendSticker sendSticker = new SendSticker();
        sendSticker.setChatId(message.getChatId().toString());
        sendSticker.setSticker(new InputFile(matcher.group(1)));

        sender.execute(sendSticker);

        return true;
    }

    public void setPattern(String pattern) {
        this.pattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
    }
}
