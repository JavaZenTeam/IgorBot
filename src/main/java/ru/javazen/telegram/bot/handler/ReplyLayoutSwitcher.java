package ru.javazen.telegram.bot.handler;

import ru.javazen.telegram.bot.BotMethodExecutor;
import ru.javazen.telegram.bot.entity.Message;
import ru.javazen.telegram.bot.entity.Update;
import ru.javazen.telegram.bot.util.MessageHelper;

public class ReplyLayoutSwitcher implements UpdateHandler {

    private String sourceCharSet = "";
    private String targetCharSet = "";

    @Override
    public boolean handle(Update update, BotMethodExecutor executor) {
        Message replyMessage = update.getMessage().getReplyToMessage();
        if (replyMessage == null) return false;

        String targetText = MessageHelper.getActualText(replyMessage);
        if (targetText == null) return false;

        String result = switchLayout(targetText);
        executor.execute(MessageHelper.answer(replyMessage, result, true), Void.class);
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
