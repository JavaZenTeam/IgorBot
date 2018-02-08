package ru.javazen.telegram.bot.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ru.javazen.telegram.bot.util.MessageHelper;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Random;
import java.util.regex.Pattern;

public class AnniversaryMessageCongratulations implements UpdateHandler {
    private Random random;
    private Pattern messageIdPattern;
    private MessageFormat[] templates;

    @Override
    public boolean handle(Update update, AbsSender sender) throws TelegramApiException {
        String messageId = update.getMessage().getMessageId().toString();
        if (messageIdPattern.matcher(messageId).matches()) {
            MessageFormat congratulationsTemplate = templates[random.nextInt(templates.length)];
            String text = congratulationsTemplate.format(new Object[]{messageId});
            SendMessage sendMessage = MessageHelper.answer(update.getMessage(), text, true);
            sender.execute(sendMessage);
        }
        return false;
    }

    @Required
    public void setMessageIdPattern(String messageIdPattern) {
        this.messageIdPattern = Pattern.compile(messageIdPattern);
    }

    @Required
    public void setTemplates(Collection<String> templates) {
        this.templates = templates.stream()
                .map(MessageFormat::new)
                .toArray(MessageFormat[]::new);
    }

    @Autowired
    public void setRandom(Random random) {
        this.random = random;
    }
}
