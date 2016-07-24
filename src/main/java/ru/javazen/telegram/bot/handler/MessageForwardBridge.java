package ru.javazen.telegram.bot.handler;

import ru.javazen.telegram.bot.Bot;
import ru.javazen.telegram.bot.entity.request.Update;
import ru.javazen.telegram.bot.entity.response.ForwardMessage;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageForwardBridge implements UpdateHandler {

    private static final Pattern DEFAULT_PATTERN = Pattern.compile("/forward_to (.+)");
    private Pattern pattern = DEFAULT_PATTERN;

    @Override
    public boolean handle(Update update, Bot bot) {
        String text = update.getMessage().getText();
        if (text == null || update.getMessage().getReplyMessage() == null) return false;
        Matcher matcher = pattern.matcher(text);
        if (!matcher.matches() || matcher.groupCount() < 1) return false;

        ForwardMessage forwardMessage = new ForwardMessage();
        forwardMessage.setFromChatId(Long.toString(update.getMessage().getChat().getId()));
        forwardMessage.setMessageId(update.getMessage().getReplyMessage().getMessageId());
        forwardMessage.setChatId(matcher.group(1));
        bot.getService().forwardMessage(forwardMessage);

        return true;
    }

    public void setPattern(String pattern) {
        this.pattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
    }
}
