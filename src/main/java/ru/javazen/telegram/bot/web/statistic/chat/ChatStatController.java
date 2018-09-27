package ru.javazen.telegram.bot.web.statistic.chat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.telegram.telegrambots.api.methods.groupadministration.GetChat;
import org.telegram.telegrambots.api.objects.Chat;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ru.javazen.telegram.bot.datasource.ChatDataSource;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;

@Controller
public class ChatStatController {
    private DefaultAbsSender bot;
    private ChatDataSource chatDataSource;

    @Autowired
    public ChatStatController(DefaultAbsSender bot, ChatDataSource chatDataSource) {
        this.bot = bot;
        this.chatDataSource = chatDataSource;
    }

    @PreAuthorize("hasAuthority(#chatIdStr)")
    @GetMapping("/chat/{chatId}")
    public String getChatView(@PathVariable("chatId") String chatIdStr, Model model,
                              @RequestParam(value = "from", required = false)
                              @DateTimeFormat(pattern = "dd.MM.yyyy")
                                      ZonedDateTime from,
                              @RequestParam(value = "to", required = false)
                              @DateTimeFormat(pattern = "dd.MM.yyyy")
                                      ZonedDateTime to) {
        Long chatId = Long.valueOf(chatIdStr);

        to = Optional.ofNullable(to).orElse(ZonedDateTime.now());
        from = Optional.ofNullable(from).orElse(to.minus(1, ChronoUnit.MONTHS));

        Date toDate = Date.from(to.toInstant());
        Date fromDate = Date.from(from.toInstant());

        model.addAttribute("topActiveUsers", chatDataSource.topActiveUsers(chatId, fromDate, toDate));

        return "chat";
    }

    @ModelAttribute("chat")
    public Chat getChat(@ModelAttribute("chatId") Long chatId) throws TelegramApiException {
        return bot.execute(new GetChat(chatId));
    }

    @ModelAttribute("bot")
    public User getBot() throws TelegramApiException {
        return bot.getMe();
    }
}