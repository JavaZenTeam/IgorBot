package ru.javazen.telegram.bot.handler.subscriptions;

import org.springframework.beans.factory.annotation.Autowired;
import ru.javazen.telegram.bot.BotMethodExecutor;
import ru.javazen.telegram.bot.entity.Message;
import ru.javazen.telegram.bot.entity.Update;
import ru.javazen.telegram.bot.handler.UpdateHandler;
import ru.javazen.telegram.bot.method.send.SendMessage;
import ru.javazen.telegram.bot.model.MessagePK;
import ru.javazen.telegram.bot.model.Subscription;
import ru.javazen.telegram.bot.service.SubscriptionService;
import ru.javazen.telegram.bot.util.MessageHelper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CreateSubscriptionHandler implements UpdateHandler {
    private SubscriptionService subscriptionService;
    private Pattern pattern;
    private String successResponse = "ok";

    @Override
    public boolean handle(Update update, BotMethodExecutor executor) {
        String text = MessageHelper.getActualText(update.getMessage());
        if (text == null) return false;
        Matcher matcher = pattern.matcher(text);
        if (!matcher.matches()) return false;

        String trigger = matcher.group("trigger");
        String response = matcher.group("response");
        String userFlag = matcher.group("userFlag");

        Subscription template = new Subscription();
        MessagePK subscriptionPK = new MessagePK(
                update.getMessage().getChat().getId(),
                update.getMessage().getMessageId());
        template.setSubscriptionPK(subscriptionPK);
        template.setTrigger("%" + trigger + "%");
        template.setResponse(response);
        if (userFlag != null) template.setUserId(update.getMessage().getFrom().getId());

        subscriptionService.createSubscription(template);

        SendMessage answer = MessageHelper.answer(update.getMessage(), successResponse);
        Message m = executor.execute(answer, Message.class);
        subscriptionService.saveSubscriptionReply(subscriptionPK, m.getMessageId());
        return true;
    }

    @Autowired
    public void setSubscriptionService(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    public void setPattern(String pattern) {
        this.pattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.DOTALL);
    }

    public void setSuccessResponse(String successResponse) {
        this.successResponse = successResponse;
    }
}
