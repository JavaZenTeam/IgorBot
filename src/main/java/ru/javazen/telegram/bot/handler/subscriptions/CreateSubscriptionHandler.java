package ru.javazen.telegram.bot.handler.subscriptions;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ru.javazen.telegram.bot.handler.UpdateHandler;
import ru.javazen.telegram.bot.handling.annotation.Handling;
import ru.javazen.telegram.bot.handling.annotation.UpdateType;
import ru.javazen.telegram.bot.model.MessagePK;
import ru.javazen.telegram.bot.model.Subscription;
import ru.javazen.telegram.bot.service.SubscriptionService;
import ru.javazen.telegram.bot.util.MessageHelper;

import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Handling(UpdateType.MESSAGE)
public class CreateSubscriptionHandler implements UpdateHandler {
    private SubscriptionService subscriptionService;
    private Pattern pattern;
    private Supplier<String> successResponseSupplier;
    private Supplier<String> tooManyDuplicatesErrorSupplier;

    @Override
    public boolean handle(Update update, AbsSender sender) throws TelegramApiException {
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
        template.setTrigger(trigger);
        template.setResponse(response);
        if (userFlag != null) template.setUserId(update.getMessage().getFrom().getId());

        try {
            subscriptionService.createSubscription(template);
            SendMessage answer = MessageHelper.answer(update.getMessage(), successResponseSupplier.get());
            Message sentMessage = sender.execute(answer);
            subscriptionService.saveSubscriptionReply(subscriptionPK, sentMessage.getMessageId());
        } catch (SubscriptionService.TooManyDuplicatesException e){
            sender.execute(MessageHelper.answer(update.getMessage(), tooManyDuplicatesErrorSupplier.get()));
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
