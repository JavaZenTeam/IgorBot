package ru.javazen.telegram.bot.web.statistic.chat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.telegram.telegrambots.api.methods.groupadministration.GetChat;
import org.telegram.telegrambots.api.objects.Chat;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ru.javazen.telegram.bot.security.authentication.AuthenticationToken;
import ru.javazen.telegram.bot.security.authentication.service.AuthenticationTokenService;

@Controller
public class ChatStatController {
    private DefaultAbsSender bot;
    private AuthenticationTokenService authenticationTokenService;

    @Autowired
    public ChatStatController(DefaultAbsSender bot, AuthenticationTokenService authenticationTokenService) {
        this.bot = bot;
        this.authenticationTokenService = authenticationTokenService;
    }

    @PreAuthorize("hasAuthority(#chatIdStr)")
    @GetMapping("/chat/{chatId}")
    public String getChatView(@PathVariable("chatId") String chatIdStr, Model model) throws TelegramApiException {
        Long chatId = Long.valueOf(chatIdStr);
        Chat chat = bot.execute(new GetChat(chatId));
        model.addAttribute("chat", chat);
        return "chat";
    }

    @GetMapping("/stats/{token}")
    public String redirectToStat(@PathVariable("token") String token) {
        AuthenticationToken authenticationToken = authenticationTokenService.findByToken(token);
        return "redirect:/chat/" + authenticationToken.getChatId();
    }

    @ModelAttribute("bot")
    public User getBot() throws TelegramApiException {
        return bot.getMe();
    }
}
