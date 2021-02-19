package ru.javazen.telegram.bot.web;

import lombok.AllArgsConstructor;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

@Controller
@AllArgsConstructor
public class ErrorPagesController implements ErrorController {
    private final DefaultAbsSender bot;

    @Override
    public String getErrorPath() {
        return "error";
    }

    @GetMapping("error")
    public String getErrorPage(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        int statusCode = Integer.parseInt(status.toString());
        model.addAttribute("httpStatus", HttpStatus.resolve(statusCode));
        return "error";
    }

    @ModelAttribute("bot")
    public User getBot() throws TelegramApiException {
        return bot.getMe();
    }
}
