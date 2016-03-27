package ru.javazen.telegram.bot.handler;

import org.springframework.beans.factory.annotation.Autowired;
import ru.javazen.telegram.bot.entity.request.Update;
import ru.javazen.telegram.bot.entity.response.ForwardMessage;
import ru.javazen.telegram.bot.method.ForwardMessageMethod;
import ru.javazen.telegram.bot.service.TelegramService;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageForwardBridge implements UpdateHandler {

    private static final Pattern DEFAULT_PATTERN = Pattern.compile("/forward_to (.+)");
    private Pattern pattern = DEFAULT_PATTERN;

    @Autowired
    private TelegramService telegramService;

    @Override
    public boolean handle(Update update, String token) {
        String text = update.getMessage().getText();
        if (text == null || update.getMessage().getReplyMessage() == null) return false;
        Matcher matcher = pattern.matcher(text);
        if (!matcher.matches() || matcher.groupCount() < 1) return false;

        ForwardMessage forwardMessage = new ForwardMessage();
        forwardMessage.setFromChatId(Long.toString(update.getMessage().getChat().getId()));
        forwardMessage.setMessageId(update.getMessage().getReplyMessage().getMessageId());
        forwardMessage.setChatId(matcher.group(1));
        telegramService.execute(new ForwardMessageMethod(forwardMessage), token);

        return true;
    }

    public void setPattern(String pattern) {
        this.pattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
    }
}
