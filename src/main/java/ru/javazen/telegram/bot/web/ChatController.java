package ru.javazen.telegram.bot.web;

import lombok.AllArgsConstructor;
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
import ru.javazen.telegram.bot.datasource.model.*;
import ru.javazen.telegram.bot.repository.MessageRepository;
import ru.javazen.telegram.bot.util.ChartDataConverter;
import ru.javazen.telegram.bot.util.DateRange;
import ru.javazen.telegram.bot.util.DateRanges;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

@Controller
@AllArgsConstructor
@RequestMapping("/chat/{chatId}/")
public class ChatController {
    private static final TimeZone DEFAULT_TIME_ZONE = TimeZone.getTimeZone("GMT +04:00"); //TODO get actual tz from user
    private DefaultAbsSender bot;
    private ChatDataSource chatDataSource;
    private ChartDataConverter chartDataConverter;
    private MessageRepository messageRepository;

    @PreAuthorize("hasAuthority(#chatIdStr)")
    @GetMapping
    public String getChatView(@PathVariable("chatId") String chatIdStr, Model model,
                              TimeZone timeZone,
                              @RequestParam(value = "range", required = false, defaultValue = "LAST_WEEK")
                                      DateRanges range,
                              @RequestParam(value = "from", required = false)
                              @DateTimeFormat(pattern = "dd.MM.yyyy")
                                      LocalDate from,
                              @RequestParam(value = "to", required = false)
                              @DateTimeFormat(pattern = "dd.MM.yyyy")
                                      LocalDate to) {
        Long chatId = Long.valueOf(chatIdStr);

        DateRange dateRange = range.apply(timeZone);
        if (range == DateRanges.CUSTOM) {
            dateRange = new DateRange(from, to, timeZone);
        } else if (range == DateRanges.ALL_TIME) {
            dateRange = new DateRange(messageRepository.startChatDate(chatId), new Date());
        }

        model.addAttribute("toDate", dateRange.getTo());
        model.addAttribute("fromDate", dateRange.getFrom());
        model.addAttribute("range", range.name());

        List<UserStatistic> topActiveUsers = chatDataSource.topActiveUsers(chatId, dateRange);
        model.addAttribute("topActiveUsers", topActiveUsers);
        model.addAttribute("totalScore", topActiveUsers.stream().mapToDouble(UserStatistic::getScore).sum());
//        model.addAttribute("botUsagesByModule", chatDataSource.botUsagesByModule(chatId, fromDate, toDate));
//        model.addAttribute("messagesCount", chatDataSource.messagesCount(chatId, fromDate, toDate));
        model.addAttribute("topStickers", chatDataSource.topStickers(chatId, dateRange, 6));
//        model.addAttribute("wordsUsageStatistic", chatDataSource.wordsUsageStatistic(chatId, dateRange));

        return "chat";
    }

    @PreAuthorize("hasAuthority(#chatIdStr)")
    @GetMapping("activity-chart")
    @ResponseBody
    public ChartData getChatActivityChart(@PathVariable("chatId") String chatIdStr,
                                          @RequestParam("interval")
                                                  int interval,
                                          @RequestParam("interval_unit")
                                                  TimeInterval.Unit unit,
                                          @RequestParam(value = "from")
                                          @DateTimeFormat(pattern = "dd.MM.yyyy")
                                                  LocalDate from,
                                          @RequestParam(value = "to")
                                          @DateTimeFormat(pattern = "dd.MM.yyyy")
                                                  LocalDate to) {
        Long chatId = Long.valueOf(chatIdStr);
        DateRange dateRange = new DateRange(from, to, DEFAULT_TIME_ZONE);
        List<PeriodUserStatistic> statistic =
                chatDataSource.activityChart(chatId, dateRange, new TimeInterval(interval, unit), DEFAULT_TIME_ZONE);
        return chartDataConverter.convert(statistic);
    }

    @PreAuthorize("hasAuthority(#chatIdStr)")
    @GetMapping("words-usage")
    @ResponseBody
    public DataTableResponse getWordUsageDataTable(@PathVariable("chatId") String chatIdStr,
                                                   @RequestParam(value = "from")
                                                   @DateTimeFormat(pattern = "dd.MM.yyyy")
                                                           LocalDate from,
                                                   @RequestParam(value = "to")
                                                   @DateTimeFormat(pattern = "dd.MM.yyyy")
                                                           LocalDate to,
                                                   DataTableRequest request) {
        Long chatId = Long.valueOf(chatIdStr);
        DateRange dateRange = new DateRange(from, to, DEFAULT_TIME_ZONE);
        return chatDataSource.wordsUsageStatistic(chatId, dateRange, request);
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
