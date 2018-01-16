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

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GetSubscriptionsByKeyHandler implements UpdateHandler {
    private SubscriptionService subscriptionService;
    private Pattern pattern;
    private String onEmptyResponse = "not found";

    @Override
    public boolean handle(Update update, BotMethodExecutor executor) {
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
            executor.execute(MessageHelper.answer(message, onEmptyResponse), Void.class);
        }

        for (Subscription s : subscriptions) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(String.valueOf(s.getSubscriptionPK().getChatId()));
            sendMessage.setReplyToMessageId(s.getSubscriptionPK().getMessageId());
            sendMessage.setText(s.getResponse());

            Message m = executor.execute(sendMessage, Message.class);
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
