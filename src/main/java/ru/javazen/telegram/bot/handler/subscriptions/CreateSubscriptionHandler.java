package ru.javazen.telegram.bot.handler.subscriptions;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.javazen.telegram.bot.handler.base.TextMessageHandler;
import ru.javazen.telegram.bot.model.MessagePK;
import ru.javazen.telegram.bot.model.Subscription;
import ru.javazen.telegram.bot.service.SubscriptionService;

import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CreateSubscriptionHandler implements TextMessageHandler {
    private SubscriptionService subscriptionService;
    private Pattern pattern;
    private Supplier<String> successResponseSupplier;
    private Supplier<String> tooManyDuplicatesErrorSupplier;

    @Override
    public boolean handle(Message message, String text, AbsSender sender) throws TelegramApiException {
        Matcher matcher = pattern.matcher(text);
        if (!matcher.matches()) return false;

        String trigger = matcher.group("trigger");
        String response = matcher.group("response");
        String userFlag = matcher.group("userFlag");

        Subscription template = new Subscription();
        MessagePK subscriptionPK = new MessagePK(
                message.getChat().getId(),
                message.getMessageId());
        template.setSubscriptionPK(subscriptionPK);
        template.setTrigger(trigger);
        template.setResponse(response);
        if (userFlag != null) template.setUserId(message.getFrom().getId());

        try {
            subscriptionService.createSubscription(template);
            SendMessage answer = new SendMessage(message.getChatId(), successResponseSupplier.get());
            Message sentMessage = sender.execute(answer);
            subscriptionService.saveSubscriptionReply(subscriptionPK, sentMessage.getMessageId());
        } catch (SubscriptionService.TooManyDuplicatesException e) {
            sender.execute(new SendMessage(message.getChatId(), tooManyDuplicatesErrorSupplier.get()));
        }
        return true;
    }

    @Autowired
    public void setSubscriptionService(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    public void setPattern(String pattern) {
        this.pattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.DOTALL);
    }

    public void setSuccessResponseSupplier(Supplier<String> successResponseSupplier) {
        this.successResponseSupplier = successResponseSupplier;
    }

    public void setTooManyDuplicatesErrorSupplier(Supplier<String> tooManyDuplicatesErrorSupplier) {
        this.tooManyDuplicatesErrorSupplier = tooManyDuplicatesErrorSupplier;
    }

    public void setSuccessResponse(String successResponse) {
        this.successResponseSupplier = () -> successResponse;
    }
}
