package ru.javazen.telegram.bot.handler.stats;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.javazen.telegram.bot.handler.base.MessageHandler;
import ru.javazen.telegram.bot.model.UserEntity;
import ru.javazen.telegram.bot.repository.UserEntityRepository;

import java.text.MessageFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ActiveUsersHandler implements MessageHandler {
    private UserEntityRepository userEntityRepository;

    @Override
    public boolean handle(Message message, AbsSender sender) throws TelegramApiException {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -7);

        List<UserEntity> activeUsers = userEntityRepository.findActiveUsers(calendar.getTime());
        String chats = activeUsers.stream()
                .map(user -> {
                    String fullName = Stream.of(user.getFirstName(), user.getLastName())
                            .filter(Objects::nonNull)
                            .collect(Collectors.joining(" "));
                    return fullName.isEmpty() ? "[id=" + user.getUserId() + "]" : fullName;
                })
                .sorted(Comparator.comparing(String::toLowerCase))
                .collect(Collectors.joining("\n"));

        String report = MessageFormat.format("Total: {0}\n\n{1}", activeUsers.size(), chats);
        sender.execute(new SendMessage(message.getChatId(), report));
        return true;
    }

    @Autowired
    public void setUserEntityRepository(UserEntityRepository userEntityRepository) {
        this.userEntityRepository = userEntityRepository;
    }
}
