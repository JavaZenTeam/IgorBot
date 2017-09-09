package ru.javazen.telegram.bot.handler;

import ru.javazen.telegram.bot.BotMethodExecutor;
import ru.javazen.telegram.bot.entity.Update;
import ru.javazen.telegram.bot.method.send.SendSticker;

public class SimpleStickerSender implements UpdateHandler {

    private String sticker;

    @Override
    public boolean handle(Update update, BotMethodExecutor executor) {
        SendSticker sendSticker = new SendSticker();
        sendSticker.setChatId(update.getMessage().getChat().getId().toString());
        sendSticker.setSticker(sticker);

        executor.execute(sendSticker, Void.class);
        return true;
    }

    public void setSticker(String sticker) {
        this.sticker = sticker;
    }
}
