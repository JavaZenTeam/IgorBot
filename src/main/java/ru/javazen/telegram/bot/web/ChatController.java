package ru.javazen.telegram.bot.web;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import ru.javazen.telegram.bot.util.*;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;

@Slf4j
@Controller
@AllArgsConstructor
@RequestMapping("/chat/{chatId}")
public class ChatController {
    private static final TimeZone DEFAULT_TIME_ZONE = TimeZone.getTimeZone("GMT+04:00"); //TODO get actual tz from user
    private static final int MAX_ACTIVITY_SIZE = 8;

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
                              @RequestParam(defaultValue = "LAST_WEEK")
                                      DateRanges range,
                              @RequestParam(required = false)
                              @DateTimeFormat(pattern = "dd.MM.yyyy")
                                      LocalDate from,
                              @RequestParam(required = false)
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
        Long scoreLimit = calcScoreLimitToFitSize(activityStatistic);
        model.addAttribute("scoreLimit", scoreLimit);
        model.addAttribute("otherSum", sumOtherStats(activityStatistic, scoreLimit));
        model.addAttribute("topStickers", dataSource.topStickers(chatId, dateRange, 6));

        Integer prevMessageCount = dataSource.messageCountByDate(chatId, dateRange.getFrom());
        Integer currMessageCount = dataSource.messageCountByDate(chatId, dateRange.getTo());
        model.addAttribute("milestoneSummary", milestoneHelper.getMilestoneSummary(prevMessageCount, currMessageCount));

        return "chat";
    }

    private Long calcScoreLimitToFitSize(List<? extends Statistic<?>> data) {
        if (data.size() > MAX_ACTIVITY_SIZE) {
            long[] values = data.stream().mapToLong(Statistic::getScorePercentage).sorted().toArray();
            long scoreLimit = values[data.size() - MAX_ACTIVITY_SIZE];
            long filtered = data.stream()
                    .filter(item -> item.getScorePercentage() <= scoreLimit)
                    .count();
            if (filtered < 3) {
                return null;
            } else {
                return scoreLimit;
            }
        } else {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private Statistic.AbstractStatistic<?> sumOtherStats(List<? extends Statistic<?>> activityStatistic, Long scoreLimit) {
        if (scoreLimit == null) {
            return null;
        }
        return activityStatistic.stream()
                .filter(item -> item.getScorePercentage() <= scoreLimit)
                .map(Statistic.AbstractStatistic.class::cast)
                .reduce((t1, t2) -> new Statistic.StringStatistic(
                        "Other",
                        t1.getCount() + t2.getCount(),
                        t1.getLength() + t2.getLength(),
                        t1.getScore() + t2.getScore(),
                        t1.getDataset())
                )
                .orElse(null);
    }

    @PreAuthorize("hasAuthority(#chatIdStr)")
    @GetMapping("activity-chart")
    @ResponseBody
    public ChartData getChatActivityChart(@PathVariable("chatId") String chatIdStr,
                                          @RequestParam(defaultValue = "SCORE")
                                                  ChartDataConverter.Attribute attribute,
                                          @RequestParam(defaultValue = "1")
                                                  int intervalQuantity,
                                          @RequestParam(defaultValue = "DAY")
                                                  TimeInterval.Unit intervalUnit,
                                          @RequestParam
                                          @DateTimeFormat(pattern = "dd.MM.yyyy")
                                                  LocalDate from,
                                          @RequestParam
                                          @DateTimeFormat(pattern = "dd.MM.yyyy")
                                                  LocalDate to,
                                          @RequestParam(required = false) String chatType) {
        Long chatId = Long.valueOf(chatIdStr);
        DateRange dateRange = new DateRange(from, to, DEFAULT_TIME_ZONE);
        TimeInterval timeInterval = new TimeInterval(intervalQuantity, intervalUnit);
        var dataSource = Objects.equals(chatType, "user") ? userDataSource : chatDataSource;
        var statistic = dataSource.activityChart(chatId, dateRange, timeInterval, DEFAULT_TIME_ZONE.toZoneId());
        return chartDataConverter.convert(statistic, attribute);
    }

    @PreAuthorize("hasAuthority(#chatIdStr)")
    @GetMapping("message-types")
    @ResponseBody
    public List<CountStatistic> getMessageTypesChart(@PathVariable("chatId") String chatIdStr,
                                                     @RequestParam
                                                     @DateTimeFormat(pattern = "dd.MM.yyyy")
                                                             LocalDate from,
                                                     @RequestParam
                                                     @DateTimeFormat(pattern = "dd.MM.yyyy")
                                                             LocalDate to,
                                                     @RequestParam(required = false) String chatType) {
        Long chatId = Long.valueOf(chatIdStr);
        DateRange dateRange = new DateRange(from, to, DEFAULT_TIME_ZONE);
        var dataSource = Objects.equals(chatType, "user") ? userDataSource : chatDataSource;
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
