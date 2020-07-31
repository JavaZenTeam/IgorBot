package ru.javazen.telegram.bot.handler;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.javazen.telegram.bot.handler.base.MessageHandler;
import ru.javazen.telegram.bot.util.MessageHelper;

public class ReplyLayoutSwitcher implements MessageHandler {

    private String sourceCharSet = "";
    private String targetCharSet = "";

    @Override
    public boolean handle(Message message, AbsSender sender) throws TelegramApiException {
        Message replyMessage = message.getReplyToMessage();
        if (replyMessage == null) return false;

        String targetText = MessageHelper.getActualText(replyMessage);
        if (targetText == null) return false;

        String result = switchLayout(targetText);
        sender.execute(new SendMessage(message.getChatId(), result).setReplyToMessageId(replyMessage.getMessageId()));
        return true;
    }

    public void setSourceCharSet(String sourceCharSet) {
        this.sourceCharSet = sourceCharSet;
    }

    public void setTargetCharSet(String targetCharSet) {
        this.targetCharSet = targetCharSet;
    }

    private String switchLayout(String text) {
        for (int i = 0; i< sourceCharSet.length() && i< targetCharSet.length(); i++) {
            text = text.replace(sourceCharSet.charAt(i), targetCharSet.charAt(i));
        }
        return text;
    }

}
