package ru.javazen.telegram.bot.handler.subscriptions;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ru.javazen.telegram.bot.handler.UpdateHandler;
import ru.javazen.telegram.bot.handling.annotation.Handling;
import ru.javazen.telegram.bot.handling.annotation.UpdateType;
import ru.javazen.telegram.bot.model.MessagePK;
import ru.javazen.telegram.bot.service.SubscriptionService;
import ru.javazen.telegram.bot.util.MessageHelper;

import java.util.function.Supplier;

@Handling(UpdateType.MESSAGE)
public class CancelSubscriptionHandler implements UpdateHandler {
    private SubscriptionService subscriptionService;
    private Supplier<String> successResponseSupplier;

    @Override
    public boolean handle(Update update, AbsSender sender) throws TelegramApiException {
        if (update.getMessage().getReplyToMessage() == null) return false;

        MessagePK messagePK = new MessagePK(
                update.getMessage().getChat().getId(),
                update.getMessage().getReplyToMessage().getMessageId());

        boolean result = subscriptionService.cancelSubscriptionByPK(messagePK)
                || subscriptionService.cancelSubscriptionByReply(messagePK);

        if (!result) return false;
        sender.execute(MessageHelper.answer(update.getMessage(), successResponseSupplier.get()));
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
