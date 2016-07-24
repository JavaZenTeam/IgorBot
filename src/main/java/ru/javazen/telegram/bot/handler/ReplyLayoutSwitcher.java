package ru.javazen.telegram.bot.handler;

import ru.javazen.telegram.bot.Bot;
import ru.javazen.telegram.bot.entity.request.Message;
import ru.javazen.telegram.bot.entity.request.Update;
import ru.javazen.telegram.bot.service.MessageHelper;

public class ReplyLayoutSwitcher implements UpdateHandler {

    private String sourceCharSet = "";
    private String targetCharSet = "";

    @Override
    public boolean handle(Update update, Bot bot) {
        String text = update.getMessage().getText();
        Message replyMessage = update.getMessage().getReplyMessage();
        if (text == null || replyMessage == null || replyMessage.getText() == null) return false;

        String result = switchLayout(replyMessage.getText());
        bot.getService().sendMessage(MessageHelper.answer(replyMessage, result, true));
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
