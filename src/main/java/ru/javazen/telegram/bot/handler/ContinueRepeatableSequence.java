package ru.javazen.telegram.bot.handler;


import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendSticker;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ru.javazen.telegram.bot.util.MessageHelper;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

public class ContinueRepeatableSequence implements UpdateHandler {
    private static final int BUFFER_SIZE = 5;
    private final Queue<Message> messageQueue = new ConcurrentLinkedQueue<>();

    @Override
    public boolean handle(Update update, AbsSender sender) throws TelegramApiException {
        Message msg = update.getMessage();
        messageQueue.add(msg);
        if (messageQueue.size() > BUFFER_SIZE){
            messageQueue.remove();
        }
        int required = generateRequiredLength();
        if (messageQueue.size() < required) {
            return false;
        }

        List<Message> messages = messageQueue.stream()
                .skip(messageQueue.size() - required)
                .collect(Collectors.toList());

        if (!allUsersUnique(messages)) {
            return false;
        }
        if (allTextsEquals(messages)){
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(msg.getChat().getId().toString());
            sendMessage.setText(MessageHelper.getActualText(msg));
            sender.execute(sendMessage);
            messageQueue.clear();
            return true;
        }
        if (allStickersEquals(messages)){
            SendSticker sendSticker = new SendSticker();
            sendSticker.setChatId(msg.getChat().getId().toString());
            sendSticker.setSticker(msg.getSticker().getFileId());
            sender.sendSticker(sendSticker);
            messageQueue.clear();
            return true;
        }
        return false;
    }

    private boolean allUsersUnique(List<Message> messages){
        long uniqueUsers = messages.stream()
                .map(Message::getFrom)
                .map(User::getId)
                .distinct()
                .count();
        return uniqueUsers == messages.size();
    }

    private boolean allTextsEquals(List<Message> messages){
        List<String> uniqueTexts = messages.stream()
                .map(MessageHelper::getActualText)
                .distinct()
                .collect(Collectors.toList());
        return uniqueTexts.size() == 1 && uniqueTexts.get(0) != null;
    }

    private boolean allStickersEquals(List<Message> messages) {
        List<String> uniqueStickers = messages.stream()
            .map(Message::getSticker)
            .map(sticker -> sticker == null ? null : sticker.getFileId())
            .distinct()
            .collect(Collectors.toList());
        return uniqueStickers.size() == 1 && uniqueStickers.get(0) != null;
    }

    private int generateRequiredLength() {
        double random = Math.random();
        if (random < 0.3) return 2;
        if (random < 0.8) return 3;
        else return 4;
    }
}
