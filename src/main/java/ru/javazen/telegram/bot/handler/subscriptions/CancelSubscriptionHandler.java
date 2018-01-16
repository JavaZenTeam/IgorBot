package ru.javazen.telegram.bot.handler.subscriptions;

import org.springframework.beans.factory.annotation.Autowired;
import ru.javazen.telegram.bot.BotMethodExecutor;
import ru.javazen.telegram.bot.entity.Update;
import ru.javazen.telegram.bot.handler.UpdateHandler;
import ru.javazen.telegram.bot.model.MessagePK;
import ru.javazen.telegram.bot.service.SubscriptionService;
import ru.javazen.telegram.bot.util.MessageHelper;

public class CancelSubscriptionHandler implements UpdateHandler {
    private SubscriptionService subscriptionService;
    private String successResponse = "Canceled Successfully";
    private String failedResponse = "Subscription not found";

    @Override
    public boolean handle(Update update, BotMethodExecutor executor) {
        MessagePK messagePK = new MessagePK(
                update.getMessage().getChat().getId(),
                update.getMessage().getReplyToMessage().getMessageId());

        boolean result = subscriptionService.cancelSubscriptionByPK(messagePK)
                || subscriptionService.cancelSubscriptionByReply(messagePK);

        String response = result ? successResponse : failedResponse;
        executor.execute(MessageHelper.answer(update.getMessage(), response), Void.class);
        return true;
    }

    @Autowired
    public void setSubscriptionService(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    public void setSuccessResponse(String successResponse) {
        this.successResponse = successResponse;
    }

    public void setFailedResponse(String failedResponse) {
        this.failedResponse = failedResponse;
    }
}
