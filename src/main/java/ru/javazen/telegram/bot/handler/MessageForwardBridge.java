package ru.javazen.telegram.bot.handler;

import org.telegram.telegrambots.meta.api.methods.ForwardMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.javazen.telegram.bot.handler.base.TextMessageHandler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageForwardBridge implements TextMessageHandler {

    private static final Pattern DEFAULT_PATTERN = Pattern.compile("/forward_to (.+)");
    private Pattern pattern = DEFAULT_PATTERN;

    @Override
    public boolean handle(Message message, String text, AbsSender sender) throws TelegramApiException {
        if (!message.isReply()) return false;

        Matcher matcher = pattern.matcher(text);
        if (!matcher.matches() || matcher.groupCount() < 1) return false;

        ForwardMessage forwardMessage = new ForwardMessage();
        forwardMessage.setFromChatId(message.getChatId());
        forwardMessage.setMessageId(message.getReplyToMessage().getMessageId());
        forwardMessage.setChatId(matcher.group(1));
        sender.execute(forwardMessage);

        return true;
    }

    public void setPattern(String pattern) {
        this.pattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
    }
}
