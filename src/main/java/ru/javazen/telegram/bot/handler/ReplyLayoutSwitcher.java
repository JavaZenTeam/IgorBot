package ru.javazen.telegram.bot.handler;

import org.springframework.beans.factory.annotation.Autowired;
import ru.javazen.telegram.bot.entity.request.Message;
import ru.javazen.telegram.bot.entity.request.Update;
import ru.javazen.telegram.bot.method.TelegramMethod;
import ru.javazen.telegram.bot.service.MessageHelper;
import ru.javazen.telegram.bot.service.TelegramService;

public class ReplyLayoutSwitcher implements UpdateHandler {

    @Autowired
    private TelegramService telegramService;

    private String sourceCharSet = "";
    private String targetChatSet = "";

    @Override
    public boolean handle(Update update, String token) {
        String text = update.getMessage().getText();
        Message replyMessage = update.getMessage().getReplyMessage();
        if (text == null || replyMessage == null || replyMessage.getText() == null) return false;

        String result = switchLayout(replyMessage.getText());
        telegramService.execute(MessageHelper.answerWithReply(replyMessage, result), token);
        return true;
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
