package ru.javazen.telegram.bot.handler;

import org.modelmapper.ModelMapper;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ru.javazen.telegram.bot.handler.base.MessageHandler;
import ru.javazen.telegram.bot.model.ChatEntity;
import ru.javazen.telegram.bot.model.ChatHistory;
import ru.javazen.telegram.bot.model.UserEntity;
import ru.javazen.telegram.bot.repository.ChatHistoryRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

public class ChatHistoryHandler implements MessageHandler {
    private ChatHistoryRepository chatHistoryRepository;
    private ModelMapper modelMapper;

    public ChatHistoryHandler(ChatHistoryRepository chatHistoryRepository, ModelMapper modelMapper) {
        this.chatHistoryRepository = chatHistoryRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public boolean handle(Message message, AbsSender sender) throws TelegramApiException {
        Collection<ChatHistory> chatHistoryCollection = new ArrayList<>();

        if (message.getNewChatTitle() != null) {
            chatHistoryCollection.add(newTitleChatHistory(message));
        } else if (message.getNewChatPhoto() != null) {
            chatHistoryCollection.add(newPhotoChatHistory(message));
        } else if (message.getDeleteChatPhoto() != null) {
            chatHistoryCollection.add(deletePhotoChatHistory(message));
        } else if (message.getNewChatMembers() != null) {
            for (User newChatMember : message.getNewChatMembers()) {
                chatHistoryCollection.add(newMemberChatHistory(message, newChatMember));
            }
        } else if (message.getLeftChatMember() != null) {
            chatHistoryCollection.add(leftMemberChatHistory(message));
        }
//        chatHistoryRepository.saveAll(chatHistoryCollection);
        return false;
    }


    private ChatHistory baseChatHistory(Message message) {
        ChatHistory chatHistory = new ChatHistory();
        chatHistory.setDate(new Date(1000L * message.getDate()));
        chatHistory.setChat(modelMapper.map(message.getChat(), ChatEntity.class));
        return chatHistory;
    }

    private ChatHistory newMemberChatHistory(Message message, User newChatMember) {
        ChatHistory chatHistory = baseChatHistory(message);
        chatHistory.setProperty("new_member");
        chatHistory.setUser(modelMapper.map(newChatMember, UserEntity.class));
        return chatHistory;
    }

    private ChatHistory leftMemberChatHistory(Message message) {
        ChatHistory chatHistory = baseChatHistory(message);
        chatHistory.setProperty("left_member");
        chatHistory.setUser(modelMapper.map(message.getLeftChatMember(), UserEntity.class));
        return chatHistory;
    }

    private ChatHistory newPhotoChatHistory(Message message) {
        ChatHistory chatHistory = baseChatHistory(message);
        chatHistory.setProperty("photo");
        chatHistory.setUser(modelMapper.map(message.getFrom(), UserEntity.class));
        chatHistory.setValue(message.getNewChatPhoto().get(0).getFileId());
        return chatHistory;
    }

    private ChatHistory deletePhotoChatHistory(Message message) {
        ChatHistory chatHistory = baseChatHistory(message);
        chatHistory.setProperty("photo");
        chatHistory.setUser(modelMapper.map(message.getFrom(), UserEntity.class));
        chatHistory.setValue(null);
        return chatHistory;
    }

    private ChatHistory newTitleChatHistory(Message message) {
        ChatHistory chatHistory = baseChatHistory(message);
        chatHistory.setProperty("title");
        chatHistory.setUser(modelMapper.map(message.getFrom(), UserEntity.class));
        chatHistory.setValue(message.getNewChatTitle());
        return chatHistory;
    }
}
