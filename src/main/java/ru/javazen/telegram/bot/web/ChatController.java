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
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;

@Slf4j
@Controller
@AllArgsConstructor
@RequestMapping("/chat/{chatId}")
public class ChatController {
    private final DefaultAbsSender bot;
    private final StatisticDataSource<UserEntity> chatDataSource;
    private final StatisticDataSource<ChatEntity> userDataSource;
    private final ChartDataConverter chartDataConverter;
    private final MessageRepository messageRepository;
    private final MilestoneHelper milestoneHelper;

    @PreAuthorize("hasAuthority('/chat/' + #chatId)")
    @GetMapping
    public String getChatView(@PathVariable Long chatId, Model model,
                              TimeZone timeZone,
                              @RequestParam(defaultValue = "LAST_WEEK")
                                      DateRanges range,
                              @RequestParam(required = false)
                              @DateTimeFormat(pattern = "dd.MM.yyyy")
                                      LocalDate from,
                              @RequestParam(required = false)
                              @DateTimeFormat(pattern = "dd.MM.yyyy")
                                      LocalDate to) throws TelegramApiException {
        DateRange dateRange = range.apply(timeZone);
        if (range == DateRanges.CUSTOM) {
            dateRange = new DateRange(from, to, timeZone);
        } else if (range == DateRanges.ALL_TIME) {
            // Date из БД уже в UTC, используем напрямую
            Date startDate = messageRepository.startChatDate(chatId);
            dateRange = new DateRange(startDate != null ? startDate : new Date(0), new Date(), timeZone);
        }

        model.addAttribute("dateRange", dateRange);
        model.addAttribute("dateRangeName", range.name());

        Chat chat = getChat(chatId);
        model.addAttribute("chat", chat);
        boolean isUserChat = Boolean.TRUE.equals(chat.isUserChat());
        StatisticDataSource<?> dataSource = isUserChat ? userDataSource : chatDataSource;

        var activityStatistic = dataSource.topActivity(chatId, dateRange);
        model.addAttribute("activityStatisticSummary", new ActivityStatisticSummary(activityStatistic, 10));
        model.addAttribute("topStickers", dataSource.topUsedStickers(chatId, dateRange, 6));

        Long prevMessageCount = dataSource.messageCountAtDate(chatId, dateRange.getFrom());
        Long currMessageCount = dataSource.messageCountAtDate(chatId, dateRange.getTo());
        model.addAttribute("milestoneSummary", milestoneHelper.getMilestoneSummary(prevMessageCount, currMessageCount));

        if (!chat.isUserChat()) {
            LocalDateTime fromDateTime = dateRange.getFrom().toInstant().atZone(timeZone.toZoneId()).toLocalDateTime();
            LocalDateTime toDateTime = dateRange.getTo().toInstant().atZone(timeZone.toZoneId()).toLocalDateTime();
            model.addAttribute("events", messageRepository.findEventsByChatId(chatId, fromDateTime, toDateTime));
        }
        return "chat";
    }

    @PreAuthorize("hasAuthority('/chat/' + #chatId)")
    @GetMapping("activity-trend-chart")
    @ResponseBody
    public ChartData getChatActivityChart(@PathVariable Long chatId,
                                          TimeZone timeZone,
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
        DateRange dateRange = new DateRange(from, to, timeZone);
        TimeInterval timeInterval = new TimeInterval(intervalQuantity, intervalUnit);
        var dataSource = Objects.equals(chatType, "user") ? userDataSource : chatDataSource;
        var statistic = dataSource.activityTrend(chatId, dateRange, timeInterval);
        return chartDataConverter.convert(statistic, attribute, timeZone.toZoneId());
    }

    @PreAuthorize("hasAuthority('/chat/' + #chatId)")
    @GetMapping("activity-bar-chart")
    @ResponseBody
    public ChartData getPeriodsBarChart(@PathVariable Long chatId,
                                        TimeZone timeZone,
                                        @RequestParam(defaultValue = "SCORE")
                                                ChartDataConverter.Attribute attribute,
                                        @RequestParam(defaultValue = "1")
                                                    int intervalQuantity,
                                        @RequestParam(defaultValue = "DAY")
                                                    TimeGroup.Field intervalUnit,
                                        @RequestParam
                                        @DateTimeFormat(pattern = "dd.MM.yyyy")
                                                LocalDate from,
                                        @RequestParam
                                        @DateTimeFormat(pattern = "dd.MM.yyyy")
                                                LocalDate to,
                                        @RequestParam(required = false) String chatType) {
        DateRange dateRange = new DateRange(from, to, timeZone);
        var dataSource = Objects.equals(chatType, "user") ? userDataSource : chatDataSource;
        TimeGroup timeGroup = new TimeGroup(intervalQuantity, intervalUnit);
        var statistic = dataSource.activityBar(chatId, dateRange, timeGroup);
        return chartDataConverter.convert(statistic, attribute, timeGroup.getNameFunc());
    }

    @PreAuthorize("hasAuthority('/chat/' + #chatId)")
    @GetMapping("message-types")
    @ResponseBody
    public List<SubjectCount<String>> getMessageTypesChart(@PathVariable Long chatId,
                                                           TimeZone timeZone,
                                                           @RequestParam
                                                           @DateTimeFormat(pattern = "dd.MM.yyyy")
                                                                   LocalDate from,
                                                           @RequestParam
                                                           @DateTimeFormat(pattern = "dd.MM.yyyy")
                                                                   LocalDate to,
                                                           @RequestParam(required = false) String chatType) {
        DateRange dateRange = new DateRange(from, to, timeZone);
        var dataSource = Objects.equals(chatType, "user") ? userDataSource : chatDataSource;
        return dataSource.messageTypesUsage(chatId, dateRange);
    }

    public Chat getChat(Long chatId) throws TelegramApiException {
        return bot.execute(new GetChat(chatId.toString()));
    }

    @ModelAttribute("bot")
    public User getBot() throws TelegramApiException {
        return bot.getMe();
    }
}
