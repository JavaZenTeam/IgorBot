package ru.javazen.telegram.bot.service;

import ru.javazen.telegram.bot.entity.request.Message;
import ru.javazen.telegram.bot.entity.request.User;
import ru.javazen.telegram.bot.entity.response.ForwardMessage;
import ru.javazen.telegram.bot.entity.response.SendMessage;
import ru.javazen.telegram.bot.entity.response.SendSticker;

public interface TelegramBotService {
    void setWebHook(String url);
    User getMe();
    Message sendMessage(SendMessage message);
    Message forwardMessage(ForwardMessage message);
    Message sendSticker(SendSticker sticker);
}
