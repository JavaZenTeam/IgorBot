package ru.javazen.telegram.bot.handler.subscriptions;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ru.javazen.telegram.bot.handler.UpdateHandler;
import ru.javazen.telegram.bot.model.MessagePK;
import ru.javazen.telegram.bot.model.Subscription;
import ru.javazen.telegram.bot.service.SubscriptionService;
import ru.javazen.telegram.bot.util.MessageHelper;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GetSubscriptionsByKeyHandler implements UpdateHandler {
    private SubscriptionService subscriptionService;
    private Pattern pattern;
    private String onEmptyResponse = "not found";

    @Override
    public boolean handle(Update update, AbsSender sender) throws TelegramApiException {
        Message message = update.getMessage();
        String text = MessageHelper.getActualText(message);
        if (text == null) return false;
        Matcher matcher = pattern.matcher(text);
        if (!matcher.matches()) return false;

        String trigger = matcher.group("trigger");

        Subscription template = new Subscription();
        template.setSubscriptionPK(new MessagePK(message.getChat().getId(), message.getMessageId()));
        template.setUserId(message.getFrom().getId());
        template.setTrigger(trigger);

        List<Subscription> subscriptions = subscriptionService.catchSubscriptions(template);

        if (subscriptions.isEmpty()){
            sender.execute(MessageHelper.answer(message, onEmptyResponse));
        }

        for (Subscription s : subscriptions) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(String.valueOf(s.getSubscriptionPK().getChatId()));
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

    public void setOnEmptyResponse(String onEmptyResponse) {
        this.onEmptyResponse = onEmptyResponse;
    }
}
