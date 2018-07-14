package ru.javazen.telegram.bot.handler;

import org.telegram.telegrambots.api.methods.ForwardMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ru.javazen.telegram.bot.handling.annotation.Handling;
import ru.javazen.telegram.bot.handling.annotation.UpdateType;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Handling(UpdateType.MESSAGE)
public class MessageForwardBridge implements UpdateHandler {

    private static final Pattern DEFAULT_PATTERN = Pattern.compile("/forward_to (.+)");
    private Pattern pattern = DEFAULT_PATTERN;

    @Override
    public boolean handle(Update update, AbsSender sender) throws TelegramApiException {
        String text = update.getMessage().getText();
        if (text == null || update.getMessage().getReplyToMessage() == null) return false;
        Matcher matcher = pattern.matcher(text);
        if (!matcher.matches() || matcher.groupCount() < 1) return false;

        ForwardMessage forwardMessage = new ForwardMessage();
        forwardMessage.setFromChatId(Long.toString(update.getMessage().getChat().getId()));
        forwardMessage.setMessageId(update.getMessage().getReplyToMessage().getMessageId());
        forwardMessage.setChatId(matcher.group(1));
        sender.execute(forwardMessage);

        return true;
    }

    public void setPattern(String pattern) {
        this.pattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
    }
}
