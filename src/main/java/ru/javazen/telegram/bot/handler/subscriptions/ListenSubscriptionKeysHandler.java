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

public class ListenSubscriptionKeysHandler implements TextMessageHandler {
    private SubscriptionService subscriptionService;

    @Override
    public boolean handle(Message message, String text, AbsSender sender) throws TelegramApiException {
        Long chatId = message.getChatId();
        List<Subscription> subscriptions = subscriptionService.catchSubscriptions(chatId, message.getFrom().getId(), text);

        for (Subscription s : subscriptions) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            sendMessage.setText(s.getResponse());

            Message m = sender.execute(sendMessage);
            subscriptionService.saveSubscriptionReply(s.getSubscriptionPK(), m.getMessageId());
        }

        return false;
    }

    @Autowired
    public void setSubscriptionService(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }
}
