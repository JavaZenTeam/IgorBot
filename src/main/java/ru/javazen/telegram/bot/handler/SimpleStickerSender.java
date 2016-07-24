package ru.javazen.telegram.bot.handler;

import ru.javazen.telegram.bot.Bot;
import ru.javazen.telegram.bot.entity.request.Update;
import ru.javazen.telegram.bot.entity.response.SendSticker;

public class SimpleStickerSender implements UpdateHandler {
    private String sticker;

    @Override
    public boolean handle(Update update, Bot bot) {
        SendSticker sendSticker = new SendSticker();
        sendSticker.setChatId(update.getMessage().getChat().getId());
        sendSticker.setSticker(sticker);

        bot.getService().sendSticker(sendSticker);

        return true;
    }

    public void setSticker(String sticker) {
        this.sticker = sticker;
    }
}
