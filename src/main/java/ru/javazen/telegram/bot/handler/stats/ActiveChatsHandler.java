package ru.javazen.telegram.bot.handler.stats;

import com.google.common.base.MoreObjects;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.javazen.telegram.bot.handler.base.MessageHandler;
import ru.javazen.telegram.bot.model.ChatEntity;
import ru.javazen.telegram.bot.repository.ChatEntityRepository;

import java.text.MessageFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ActiveChatsHandler implements MessageHandler {
    private ChatEntityRepository chatEntityRepository;

    @Override
    public boolean handle(Message message, AbsSender sender) throws TelegramApiException {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -7);

        List<ChatEntity> activeChats = chatEntityRepository.findActiveChats(calendar.getTime());
        String chats = activeChats.stream()
                .map(chat -> MoreObjects.firstNonNull(chat.getTitle(), "[id=" + chat.getChatId() + "]"))
                .sorted(Comparator.comparing(String::toLowerCase))
                .collect(Collectors.joining("\n"));

        String report = MessageFormat.format("Total: {0}\n\n{1}", activeChats.size(), chats);
        sender.execute(new SendMessage(message.getChatId(), report));
        return true;
    }

    @Autowired
    public void setChatEntityRepository(ChatEntityRepository chatEntityRepository) {
        this.chatEntityRepository = chatEntityRepository;
    }
}
