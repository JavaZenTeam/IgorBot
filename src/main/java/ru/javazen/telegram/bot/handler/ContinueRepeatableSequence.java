package ru.javazen.telegram.bot.handler;

import ru.javazen.telegram.bot.BotMethodExecutor;
import ru.javazen.telegram.bot.entity.Message;
import ru.javazen.telegram.bot.entity.Sticker;
import ru.javazen.telegram.bot.entity.Update;
import ru.javazen.telegram.bot.method.send.SendSticker;
import ru.javazen.telegram.bot.util.MessageHelper;

import java.util.Objects;

public class ContinueRepeatableSequence implements UpdateHandler {
    private volatile Message lastMessage;

    @Override
    public boolean handle(Update update, BotMethodExecutor executor) {
        boolean handled = false;
        Message newMessage = update.getMessage();
        if (lastMessage != null && !equalAuthor(newMessage)) {
            if (equalText(newMessage)) {
                String text = MessageHelper.getActualText(newMessage);
                executor.execute(MessageHelper.answer(newMessage, text), Void.class);
                handled = true;
            }

            if (equalSticker(newMessage)) {
                SendSticker sendSticker = new SendSticker();
                sendSticker.setChatId(newMessage.getChat().getId().toString());
                sendSticker.setSticker(newMessage.getSticker().getFileId());
                executor.execute(sendSticker, Void.class);
                handled = true;
            }
        }
        lastMessage = newMessage;
        return handled;
    }

    private boolean equalText(Message newMessage) {
        String lastText = MessageHelper.getActualText(lastMessage);
        String newText = MessageHelper.getActualText(newMessage);
        return lastText != null && lastText.equals(newText);
    }

    private boolean equalSticker(Message newMessage) {
        Sticker lastSticker = lastMessage.getSticker();
        Sticker newSticker = newMessage.getSticker();
        return lastSticker != null && lastSticker.getFileId().equals(newSticker.getFileId());
    }

    private boolean equalAuthor(Message newMessage){
        return Objects.equals(lastMessage.getFrom().getId(), newMessage.getFrom().getId());
    }
}
