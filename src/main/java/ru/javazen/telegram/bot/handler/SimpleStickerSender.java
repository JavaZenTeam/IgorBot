package ru.javazen.telegram.bot.handler;

import org.springframework.beans.factory.annotation.Autowired;
import ru.javazen.telegram.bot.entity.request.Update;
import ru.javazen.telegram.bot.entity.response.SendSticker;
import ru.javazen.telegram.bot.method.SendStickerMethod;
import ru.javazen.telegram.bot.method.TelegramMethod;
import ru.javazen.telegram.bot.service.TelegramService;

public class SimpleStickerSender implements UpdateHandler {
    private String sticker;

    @Autowired
    private TelegramService telegramService;

    @Override
    public boolean handle(Update update, String token) {
        SendSticker sendSticker = new SendSticker();
        sendSticker.setChatId(update.getMessage().getChat().getId());
        sendSticker.setSticker(sticker);

        telegramService.execute(new SendStickerMethod(sendSticker), token);

        return true;
    }

    public void setSticker(String sticker) {
        this.sticker = sticker;
    }
}
