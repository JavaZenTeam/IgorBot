package ru.javazen.telegram.bot.handler;

import org.telegram.telegrambots.api.methods.send.SendSticker;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ru.javazen.telegram.bot.handler.base.MessageHandler;

public class SimpleStickerSender implements MessageHandler {

    private String sticker;

    @Override
    public boolean handle(Message message, AbsSender sender) throws TelegramApiException {
        SendSticker sendSticker = new SendSticker();
        sendSticker.setChatId(message.getChatId());
        sendSticker.setSticker(sticker);

        sender.sendSticker(sendSticker);
        return true;
    }

    public void setSticker(String sticker) {
        this.sticker = sticker;
    }
}
