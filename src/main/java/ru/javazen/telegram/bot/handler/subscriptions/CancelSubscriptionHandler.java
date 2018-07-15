package ru.javazen.telegram.bot.handler.subscriptions;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ru.javazen.telegram.bot.handler.base.MessageHandler;
import ru.javazen.telegram.bot.model.MessagePK;
import ru.javazen.telegram.bot.service.SubscriptionService;

import java.util.function.Supplier;

public class CancelSubscriptionHandler implements MessageHandler {
    private SubscriptionService subscriptionService;
    private Supplier<String> successResponseSupplier;

    @Override
    public boolean handle(Message message, AbsSender sender) throws TelegramApiException {
        if (message.getReplyToMessage() == null) return false;

        MessagePK messagePK = new MessagePK(
                message.getChatId(),
                message.getReplyToMessage().getMessageId());

        boolean canceled = subscriptionService.cancelSubscriptionByPK(messagePK)
                || subscriptionService.cancelSubscriptionByReply(messagePK);

        if (!canceled) return false;
        sender.execute(new SendMessage(message.getChatId(), successResponseSupplier.get()));
        return true;
    }

    @Autowired
    public void setSubscriptionService(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    public void setSuccessResponseSupplier(Supplier<String> successResponseSupplier) {
        this.successResponseSupplier = successResponseSupplier;
    }

    public void setSuccessResponse(String successResponse) {
        this.successResponseSupplier = () -> successResponse;
    }
}
