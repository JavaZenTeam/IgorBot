package ru.javazen.telegram.bot.scheduler;

import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatAdministrators;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.javazen.telegram.bot.handler.base.TextMessageHandler;
import ru.javazen.telegram.bot.scheduler.service.MessageSchedulerService;

import java.util.List;
import java.util.function.Supplier;

public class UnschedulerNotifyHandler implements TextMessageHandler {

    private MessageSchedulerService messageSchedulerService;
    private Supplier<String> successResponseSupplier;

    public UnschedulerNotifyHandler(MessageSchedulerService messageSchedulerService,
                                    Supplier<String> successResponseSupplier) {
        this.messageSchedulerService = messageSchedulerService;
        this.successResponseSupplier = successResponseSupplier;
    }

    @Override
    public boolean handle(Message message, String text, AbsSender sender) throws TelegramApiException {
        if (!message.isReply() || !canCancelTask(message, sender)) {
            return false;
        }

        boolean canceled = messageSchedulerService.cancelTaskByChatAndMessage(message.getChatId(),
                    message.getReplyToMessage().getMessageId());

        if (canceled) {
            SendMessage sendMessage = new SendMessage(message.getChatId().toString(),
                    successResponseSupplier.get());
            sendMessage.setMessageThreadId(message.getMessageThreadId());
            sender.execute(sendMessage);
        }

        return canceled;
    }

    private boolean canCancelTask(Message message, AbsSender sender) throws TelegramApiException {
        return isReplyAuthor(message) || isChatAdmin(message, sender);
    }

    private boolean isReplyAuthor(Message message) {
        return message.getFrom().getId().equals(message.getReplyToMessage().getFrom().getId());
    }

    private boolean isChatAdmin(Message message, AbsSender sender) throws TelegramApiException {
        Chat chat = message.getChat();
        if (chat != null && chat.isUserChat()) {
            return true;
        }

        GetChatAdministrators getChatAdministrators = new GetChatAdministrators();
        getChatAdministrators.setChatId(message.getChatId());

        List<ChatMember> administrators = sender.execute(getChatAdministrators);
        return administrators.stream()
                .map(ChatMember::getUser)
                .anyMatch(user -> message.getFrom().getId().equals(user.getId()));
    }
}

