package ru.javazen.telegram.bot.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@Controller
public class ErrorPagesController extends BasicErrorController {
    private final DefaultAbsSender bot;

    public ErrorPagesController(ErrorAttributes errorAttributes, ServerProperties serverProperties, DefaultAbsSender bot) {
        super(errorAttributes, serverProperties.getError());
        this.bot = bot;
    }

    @ModelAttribute("bot")
    public User getBot() throws TelegramApiException {
        return bot.getMe();
    }
}
