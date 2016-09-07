package ru.javazen.telegram.bot.handler;

import org.springframework.beans.factory.annotation.Autowired;
import ru.javazen.telegram.bot.Bot;
import ru.javazen.telegram.bot.entity.request.Update;
import ru.javazen.telegram.bot.entity.response.SendSticker;
import ru.javazen.telegram.bot.service.TelegramBotService;

public class SimpleStickerSender implements UpdateHandler {

    @Autowired
    private TelegramBotService botService;

    private String sticker;

    @Override
    public boolean handle(Update update) {
        SendSticker sendSticker = new SendSticker();
        sendSticker.setChatId(update.getMessage().getChat().getId());
        sendSticker.setSticker(sticker);

        botService.sendSticker(sendSticker);

        return true;
    }

    public void setSticker(String sticker) {
        this.sticker = sticker;
    }
}
