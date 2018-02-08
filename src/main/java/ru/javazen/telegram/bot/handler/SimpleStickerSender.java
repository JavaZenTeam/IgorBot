package ru.javazen.telegram.bot.handler;

import org.telegram.telegrambots.api.methods.send.SendSticker;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;

public class SimpleStickerSender implements UpdateHandler {

    private String sticker;

    @Override
    public boolean handle(Update update, AbsSender sender) throws TelegramApiException {
        SendSticker sendSticker = new SendSticker();
        sendSticker.setChatId(update.getMessage().getChat().getId().toString());
        sendSticker.setSticker(sticker);

        sender.sendSticker(sendSticker);
        return true;
    }

    public void setSticker(String sticker) {
        this.sticker = sticker;
    }
}
