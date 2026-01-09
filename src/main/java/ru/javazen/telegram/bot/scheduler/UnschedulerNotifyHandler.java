package ru.javazen.telegram.bot.scheduler;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.javazen.telegram.bot.handler.base.TextMessageHandler;
import ru.javazen.telegram.bot.scheduler.service.MessageSchedulerService;

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
        if (!message.isReply() || !isReplyAuthor(message)) {
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

    private boolean isReplyAuthor(Message message) {
        return message.getFrom().getId().equals(message.getReplyToMessage().getFrom().getId());
    }
}

