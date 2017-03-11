package ru.javazen.telegram.bot.handler;

import org.springframework.beans.factory.annotation.Autowired;
import ru.javazen.telegram.bot.entity.request.Message;
import ru.javazen.telegram.bot.entity.request.Update;
import ru.javazen.telegram.bot.service.MessageHelper;
import ru.javazen.telegram.bot.service.TelegramBotService;

public class ReplyLayoutSwitcher implements UpdateHandler {

    @Autowired
    private TelegramBotService botService;

    private String sourceCharSet = "";
    private String targetCharSet = "";

    @Override
    public boolean handle(Update update) {
        Message replyMessage = update.getMessage().getReplyMessage();
        if (replyMessage == null) return false;

        String targetText = MessageHelper.getActualText(replyMessage);
        if (targetText == null) return false;

        String result = switchLayout(targetText);
        botService.sendMessage(MessageHelper.answer(replyMessage, result, true));
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
