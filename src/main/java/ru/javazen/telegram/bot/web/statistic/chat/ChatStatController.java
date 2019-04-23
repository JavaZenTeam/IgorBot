package ru.javazen.telegram.bot.web.statistic.chat;

import lombok.AllArgsConstructor;
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
import ru.javazen.telegram.bot.datasource.model.UserStatistic;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Controller
@AllArgsConstructor
public class ChatStatController {
    private DefaultAbsSender bot;
    private ChatDataSource chatDataSource;

    @PreAuthorize("hasAuthority(#chatIdStr)")
    @GetMapping("/chat/{chatId}")
    public String getChatView(@PathVariable("chatId") String chatIdStr, Model model,
                              @RequestParam(value = "from", required = false)
                              @DateTimeFormat(pattern = "dd.MM.yyyy") LocalDate from,
                              @RequestParam(value = "to", required = false)
                              @DateTimeFormat(pattern = "dd.MM.yyyy") LocalDate to) {
        Long chatId = Long.valueOf(chatIdStr);

        to = Optional.ofNullable(to).orElse(LocalDate.now());
        from = Optional.ofNullable(from).orElse(to.minus(1, ChronoUnit.MONTHS));

        Date toDate = Date.from(to.atStartOfDay(ZoneId.systemDefault()).plusDays(1).toInstant());
        Date fromDate = Date.from(from.atStartOfDay(ZoneId.systemDefault()).toInstant());

        List<UserStatistic> topActiveUsers = chatDataSource.topActiveUsers(chatId, fromDate, toDate);
        model.addAttribute("topActiveUsers", topActiveUsers);
        model.addAttribute("totalScore", topActiveUsers.stream().mapToDouble(UserStatistic::getScore).sum());
//        model.addAttribute("botUsagesByModule", chatDataSource.botUsagesByModule(chatId, fromDate, toDate));
        model.addAttribute("wordsUsageStatistic", chatDataSource.wordsUsageStatistic(chatId, fromDate, toDate));
//        model.addAttribute("messagesCount", chatDataSource.messagesCount(chatId, fromDate, toDate));
        model.addAttribute("topStickers", chatDataSource.topStickers(chatId, fromDate, toDate));
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
