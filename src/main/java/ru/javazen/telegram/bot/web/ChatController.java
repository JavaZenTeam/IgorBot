package ru.javazen.telegram.bot.web;

import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChat;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.javazen.telegram.bot.datasource.StatisticDataSource;
import ru.javazen.telegram.bot.datasource.model.*;
import ru.javazen.telegram.bot.model.ChatEntity;
import ru.javazen.telegram.bot.model.UserEntity;
import ru.javazen.telegram.bot.repository.MessageRepository;
import ru.javazen.telegram.bot.util.ChartDataConverter;
import ru.javazen.telegram.bot.util.DateRange;
import ru.javazen.telegram.bot.util.DateRanges;
import ru.javazen.telegram.bot.util.MilestoneHelper;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;

@Controller
@AllArgsConstructor
@RequestMapping("/chat/{chatId}/")
public class ChatController {
    private static final TimeZone DEFAULT_TIME_ZONE = TimeZone.getTimeZone("GMT+04:00"); //TODO get actual tz from user
    private final DefaultAbsSender bot;
    private final StatisticDataSource<UserEntity> chatDataSource;
    private final StatisticDataSource<ChatEntity> userDataSource;
    private final ChartDataConverter chartDataConverter;
    private final MessageRepository messageRepository;
    private final MilestoneHelper milestoneHelper;

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
                                      LocalDate to) throws TelegramApiException {
        Long chatId = Long.valueOf(chatIdStr);
        DateRange dateRange = range.apply(timeZone);
        if (range == DateRanges.CUSTOM) {
            dateRange = new DateRange(from, to, timeZone);
        } else if (range == DateRanges.ALL_TIME) {
            dateRange = new DateRange(messageRepository.startChatDate(chatId), new Date(), timeZone);
        }

        model.addAttribute("dateRange", dateRange);
        model.addAttribute("dateRangeName", range.name());

        Chat chat = getChat(chatId);
        model.addAttribute("chat", chat);
        boolean isUserChat = Boolean.TRUE.equals(chat.isUserChat());
        StatisticDataSource<?> dataSource = isUserChat ? userDataSource : chatDataSource;

        var activityStatistic = dataSource.topActivity(chatId, dateRange);
        model.addAttribute("activityStatistic", activityStatistic);
        model.addAttribute("totalScore", activityStatistic.stream().mapToDouble(Statistic::getScore).sum());
        model.addAttribute("topStickers", dataSource.topStickers(chatId, dateRange, 6));

        Integer prevMessageCount = dataSource.messageCountByDate(chatId, dateRange.getFrom());
        Integer currMessageCount = dataSource.messageCountByDate(chatId, dateRange.getTo());
        model.addAttribute("milestoneSummary", milestoneHelper.getMilestoneSummary(prevMessageCount, currMessageCount));

        return "chat";
    }

    @PreAuthorize("hasAuthority(#chatIdStr)")
    @GetMapping("activity-chart")
    @ResponseBody
    public ChartData getChatActivityChart(@PathVariable("chatId") String chatIdStr,
                                          @RequestParam(value = "attribute", defaultValue = "SCORE")
                                                  ChartDataConverter.Attribute attribute,
                                          @RequestParam("interval")
                                                  int interval,
                                          @RequestParam("interval_unit")
                                                  TimeInterval.Unit unit,
                                          @RequestParam(value = "from")
                                          @DateTimeFormat(pattern = "dd.MM.yyyy")
                                                  LocalDate from,
                                          @RequestParam(value = "to")
                                          @DateTimeFormat(pattern = "dd.MM.yyyy")
                                                  LocalDate to,
                                          @RequestParam String type) {
        Long chatId = Long.valueOf(chatIdStr);
        DateRange dateRange = new DateRange(from, to, DEFAULT_TIME_ZONE);
        TimeInterval timeInterval = new TimeInterval(interval, unit);
        var dataSource = Objects.equals(type, "user") ? userDataSource : chatDataSource;
        var statistic = dataSource.activityChart(chatId, dateRange, timeInterval, DEFAULT_TIME_ZONE);
        return chartDataConverter.convert(statistic, attribute);
    }

    @PreAuthorize("hasAuthority(#chatIdStr)")
    @GetMapping("message-types")
    @ResponseBody
    public List<CountStatistic> getMessageTypesChart(@PathVariable("chatId") String chatIdStr,
                                                     @RequestParam(value = "from")
                                                     @DateTimeFormat(pattern = "dd.MM.yyyy")
                                                             LocalDate from,
                                                     @RequestParam(value = "to")
                                                     @DateTimeFormat(pattern = "dd.MM.yyyy")
                                                             LocalDate to,
                                                     @RequestParam String type) {
        Long chatId = Long.valueOf(chatIdStr);
        DateRange dateRange = new DateRange(from, to, DEFAULT_TIME_ZONE);
        var dataSource = Objects.equals(type, "user") ? userDataSource : chatDataSource;
        return dataSource.messageTypesChart(chatId, dateRange);
    }

    public Chat getChat(Long chatId) throws TelegramApiException {
        return bot.execute(new GetChat(chatId));
    }

    @ModelAttribute("bot")
    public User getBot() throws TelegramApiException {
        return bot.getMe();
    }
}
