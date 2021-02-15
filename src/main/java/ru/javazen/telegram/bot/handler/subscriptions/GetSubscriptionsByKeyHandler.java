package ru.javazen.telegram.bot.handler.subscriptions;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.javazen.telegram.bot.handler.base.TextMessageHandler;
import ru.javazen.telegram.bot.model.Subscription;
import ru.javazen.telegram.bot.service.SubscriptionService;

import java.util.List;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GetSubscriptionsByKeyHandler implements TextMessageHandler {
    private SubscriptionService subscriptionService;
    private Pattern pattern;
    private Supplier<String> onEmptyResponseSupplier;

    @Override
    public boolean handle(Message message, String text, AbsSender sender) throws TelegramApiException {
        if (text == null) return false;
        Matcher matcher = pattern.matcher(text);
        if (!matcher.matches()) return false;

        String trigger = matcher.group("trigger");
        Long chatId = message.getChat().getId();
        List<Subscription> subscriptions = subscriptionService.catchSubscriptions(chatId, message.getFrom().getId(), trigger);

        if (subscriptions.isEmpty()){
            sender.execute(new SendMessage(message.getChatId(), onEmptyResponseSupplier.get()));
        }

        for (Subscription s : subscriptions) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            sendMessage.setReplyToMessageId(s.getSubscriptionPK().getMessageId());
            sendMessage.setText(s.getResponse());

            Message m = sender.execute(sendMessage);
            subscriptionService.saveSubscriptionReply(s.getSubscriptionPK(), m.getMessageId());
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

    public void setOnEmptyResponseSupplier(Supplier<String> onEmptyResponseSupplier) {
        this.onEmptyResponseSupplier = onEmptyResponseSupplier;
    }

    public void setOnEmptyResponse(String onEmptyResponse) {
        this.onEmptyResponseSupplier = () -> onEmptyResponse;
    }
}
