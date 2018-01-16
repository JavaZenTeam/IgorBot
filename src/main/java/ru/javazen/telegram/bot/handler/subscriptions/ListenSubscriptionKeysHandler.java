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

public class ListenSubscriptionKeysHandler implements UpdateHandler {
    private SubscriptionService subscriptionService;

    @Override
    public boolean handle(Update update, BotMethodExecutor executor) {
        Message message = update.getMessage();
        String text = MessageHelper.getActualText(message);
        if (text == null) return false;

        Subscription template = new Subscription();
        template.setSubscriptionPK(new MessagePK(message.getChat().getId(), message.getMessageId()));
        template.setUserId(message.getFrom().getId());
        template.setTrigger(text);

        List<Subscription> subscriptions = subscriptionService.catchSubscriptions(template);

        for (Subscription s : subscriptions) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(String.valueOf(s.getSubscriptionPK().getChatId()));
            sendMessage.setText(s.getResponse());

            Message m = executor.execute(sendMessage, Message.class);
            subscriptionService.saveSubscriptionReply(s.getSubscriptionPK(), m.getMessageId());
        }

        return false;
    }

    @Autowired
    public void setSubscriptionService(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }
}
