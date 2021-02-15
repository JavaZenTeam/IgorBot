package ru.javazen.telegram.bot.handler;

import lombok.Setter;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.javazen.telegram.bot.handler.base.MessageHandler;
import ru.javazen.telegram.bot.util.MilestoneHelper;

import java.text.MessageFormat;
import java.util.function.Supplier;

@Setter
public class MilestoneCongratulations implements MessageHandler {
    private Supplier<String> templateSupplier;
    private MilestoneHelper milestoneHelper;

    @Override
    public boolean handle(Message message, AbsSender sender) throws TelegramApiException {
        if (!message.getChat().isSuperGroupChat()) {
            return false; //message id works as counter starting from 1 only in Super groups
        }
        if (milestoneHelper.isMilestone(message.getMessageId())) {
            String text = MessageFormat.format(templateSupplier.get(), message.getMessageId());
            sender.execute(new SendMessage(message.getChatId(), text).setReplyToMessageId(message.getMessageId()));
        }
        return false;
    }
}
