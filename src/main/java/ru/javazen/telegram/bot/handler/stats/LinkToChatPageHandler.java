package ru.javazen.telegram.bot.handler.stats;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ru.javazen.telegram.bot.handler.base.MessageHandler;
import ru.javazen.telegram.bot.security.authentication.service.AuthenticationTokenService;

import java.text.MessageFormat;

public class LinkToChatPageHandler implements MessageHandler {
    private String linkTemplate;

    @Autowired
    private AuthenticationTokenService authenticationTokenService;

    public LinkToChatPageHandler(String linkTemplate) {
        this.linkTemplate = linkTemplate;
    }

    @Override
    public boolean handle(Message message, AbsSender sender) throws TelegramApiException {
        String token = authenticationTokenService.generateToken(message.getChatId()).getToken();

        String link = MessageFormat.format(linkTemplate, token);
        sender.execute(new SendMessage(message.getChatId(), link));
        return true;
    }
}
