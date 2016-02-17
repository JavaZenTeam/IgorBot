package ru.javazen.telegram.bot.handler;

import ru.javazen.telegram.bot.entity.request.Message;
import ru.javazen.telegram.bot.entity.request.Update;
import ru.javazen.telegram.bot.method.TelegramMethod;
import ru.javazen.telegram.bot.service.MessageHelper;

public class ReplyLayoutSwitcher implements UpdateHandler {

    private String sourceCharSet = "";
    private String targetChatSet = "";

    public TelegramMethod handle(Update update) {
        String text = update.getMessage().getText();
        Message replyMessage = update.getMessage().getReplyMessage();
        if (text == null || replyMessage == null || replyMessage.getText() == null) return null;

        String result = switchLayout(replyMessage.getText());
        return MessageHelper.answerWithReply(replyMessage, result);
    }

    public void setSourceCharSet(String sourceCharSet) {
        this.sourceCharSet = sourceCharSet;
    }

    public void setTargetChatSet(String targetChatSet) {
        this.targetChatSet = targetChatSet;
    }

    private String switchLayout(String text) {
        for (int i = 0; i< sourceCharSet.length() && i< targetChatSet.length(); i++) {
            text = text.replace(sourceCharSet.charAt(i), targetChatSet.charAt(i));
        }
        return text;
    }

}
