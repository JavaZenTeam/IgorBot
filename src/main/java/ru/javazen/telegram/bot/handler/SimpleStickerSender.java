package ru.javazen.telegram.bot.handler;

import ru.javazen.telegram.bot.entity.request.Update;
import ru.javazen.telegram.bot.entity.response.SendSticker;
import ru.javazen.telegram.bot.method.SendStickerMethod;
import ru.javazen.telegram.bot.method.TelegramMethod;

public class SimpleStickerSender implements UpdateHandler {
    private String sticker;

    public TelegramMethod handle(Update update) {
        SendSticker sendSticker = new SendSticker();
        sendSticker.setChatId(update.getMessage().getChat().getId());
        sendSticker.setSticker(sticker);
        return new SendStickerMethod(sendSticker);
    }

    public void setSticker(String sticker) {
        this.sticker = sticker;
    }
}
