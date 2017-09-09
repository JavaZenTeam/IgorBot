package ru.javazen.telegram.bot.handler;

import ru.javazen.telegram.bot.BotMethodExecutor;
import ru.javazen.telegram.bot.entity.Update;
import ru.javazen.telegram.bot.method.send.ForwardMessage;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageForwardBridge implements UpdateHandler {

    private static final Pattern DEFAULT_PATTERN = Pattern.compile("/forward_to (.+)");
    private Pattern pattern = DEFAULT_PATTERN;

    @Override
    public boolean handle(Update update, BotMethodExecutor executor) {
        String text = update.getMessage().getText();
        if (text == null || update.getMessage().getReplyToMessage() == null) return false;
        Matcher matcher = pattern.matcher(text);
        if (!matcher.matches() || matcher.groupCount() < 1) return false;

        ForwardMessage forwardMessage = new ForwardMessage();
        forwardMessage.setFromChatId(Long.toString(update.getMessage().getChat().getId()));
        forwardMessage.setMessageId(update.getMessage().getReplyToMessage().getMessageId());
        forwardMessage.setChatId(matcher.group(1));
        executor.execute(forwardMessage, Void.class);

        return true;
    }

    public void setPattern(String pattern) {
        this.pattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
    }
}
