package ru.javazen.telegram.bot.web.statistic.chat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.telegram.telegrambots.api.methods.groupadministration.GetChat;
import org.telegram.telegrambots.api.objects.Chat;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ru.javazen.telegram.bot.datasource.ChatDataSource;
import ru.javazen.telegram.bot.datasource.model.UserStatistic;
import ru.javazen.telegram.bot.security.authentication.AuthenticationToken;
import ru.javazen.telegram.bot.security.authentication.service.AuthenticationTokenService;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Controller
public class ChatStatController {
    private DefaultAbsSender bot;
    private AuthenticationTokenService authenticationTokenService;
    private ChatDataSource chatDataSource;

    @Autowired
    public ChatStatController(DefaultAbsSender bot, AuthenticationTokenService authenticationTokenService, ChatDataSource chatDataSource) {
        this.bot = bot;
        this.authenticationTokenService = authenticationTokenService;
        this.chatDataSource = chatDataSource;
    }

    @PreAuthorize("hasAuthority(#chatIdStr)")
    @GetMapping("/chat/{chatId}")
    public String getChatView(@PathVariable("chatId") String chatIdStr, Model model) throws TelegramApiException {
        Long chatId = Long.valueOf(chatIdStr);
        Chat chat = bot.execute(new GetChat(chatId));
        model.addAttribute("chat", chat);
        return "chat";
    }

    @PreAuthorize("hasAuthority(#chatIdStr)")
    @GetMapping("/chat/{chatId}/topActiveUsers")
    @ResponseBody
    public List<UserStatistic> topActiveUsers(
            @PathVariable("chatId")
                    String chatIdStr,
            @RequestParam(value = "fromDate", required = false)
            @DateTimeFormat(pattern = "dd.MM.yyyy")
                    Date fromDate,
            @RequestParam(value = "toDate", required = false)
            @DateTimeFormat(pattern = "dd.MM.yyyy")
                    Date toDate) {

        Long chatId = Long.valueOf(chatIdStr);

        if (toDate == null) {
            toDate = new Date();
        }
        if (fromDate == null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(toDate);
            calendar.add(Calendar.MONTH, -1);
            fromDate = calendar.getTime();
        }

        return chatDataSource.topActiveUsers(chatId, fromDate, toDate);
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
