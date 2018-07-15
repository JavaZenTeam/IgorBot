package ru.javazen.telegram.bot.handler;


import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendSticker;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ru.javazen.telegram.bot.handler.base.MessageHandler;

import java.util.Collection;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ContinueRepeatableSequence implements MessageHandler {
    private static final int BUFFER_SIZE = 5;
    private final Queue<Message> messageQueue = new ConcurrentLinkedQueue<>();

    @Override
    public boolean handle(Message message, AbsSender sender) throws TelegramApiException {
        messageQueue.add(message);
        if (messageQueue.size() > BUFFER_SIZE) {
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
        if (allTextsEquals(messages)) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(message.getChatId());
            sendMessage.setText(message.getText());
            sender.execute(sendMessage);
            messageQueue.clear();
            return true;
        }
        if (allStickersEquals(messages)) {
            SendSticker sendSticker = new SendSticker();
            sendSticker.setChatId(message.getChatId());
            sendSticker.setSticker(message.getSticker().getFileId());
            sender.sendSticker(sendSticker);
            messageQueue.clear();
            return true;
        }
        return false;
    }

    private boolean allUsersUnique(List<Message> messages) {
        long uniqueUsers = messages.stream()
                .map(Message::getFrom)
                .map(User::getId)
                .distinct()
                .count();
        return uniqueUsers == messages.size();
    }

    private boolean allTextsEquals(List<Message> messages) {
        return allEquals(messages, Message::getText);
    }

    private boolean allStickersEquals(List<Message> messages) {
        return allEquals(messages, message -> message.getSticker() != null ? message.getSticker().getFileId() : null);
    }

    private <T, V> boolean allEquals(Collection<T> items, Function<T, V> path) {
        Object[] unique = items.stream().map(path).distinct().toArray();
        return unique.length == 1 && unique[0] != null;
    }

    private int generateRequiredLength() {
        double random = Math.random();
        if (random < 0.3) return 2;
        if (random < 0.8) return 3;
        else return 4;
    }
}
