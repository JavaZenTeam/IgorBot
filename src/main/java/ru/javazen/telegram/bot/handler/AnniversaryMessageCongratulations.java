package ru.javazen.telegram.bot.handler;

import org.springframework.beans.factory.annotation.Required;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.javazen.telegram.bot.handler.base.MessageHandler;

import java.text.MessageFormat;
import java.util.function.Supplier;
import java.util.regex.Pattern;

public class AnniversaryMessageCongratulations implements MessageHandler {
    private Pattern messageIdPattern;
    private Supplier<String> templateSupplier;

    @Override
    public boolean handle(Message message, AbsSender sender) throws TelegramApiException {
        if (messageIdPattern.matcher(message.getMessageId().toString()).matches()) {
            String text = MessageFormat.format(templateSupplier.get(), message.getMessageId());
            sender.execute(new SendMessage(message.getChatId(), text).setReplyToMessageId(message.getMessageId()));
        }
        return false;
    }

    @Required
    public void setMessageIdPattern(String messageIdPattern) {
        this.messageIdPattern = Pattern.compile(messageIdPattern);
    }

    @Required
    public void setTemplateSupplier(Supplier<String> templateSupplier) {
        this.templateSupplier = templateSupplier;
    }
}
