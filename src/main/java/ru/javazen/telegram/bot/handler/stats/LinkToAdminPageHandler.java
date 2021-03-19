package ru.javazen.telegram.bot.handler.stats;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.javazen.telegram.bot.handler.base.MessageHandler;
import ru.javazen.telegram.bot.security.authentication.service.AuthenticationTokenService;

import java.text.MessageFormat;

@RequiredArgsConstructor
public class LinkToAdminPageHandler implements MessageHandler {
    private final String linkTemplate;

    @Autowired
    private AuthenticationTokenService authenticationTokenService;

    @Override
    public boolean handle(Message message, AbsSender sender) throws TelegramApiException {
        String token = authenticationTokenService.generateToken("/admin").getToken();
        String link = MessageFormat.format(linkTemplate, token);
        sender.execute(new SendMessage(message.getChatId(), link));
        return true;
    }
}
