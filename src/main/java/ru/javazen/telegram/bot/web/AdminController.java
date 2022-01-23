package ru.javazen.telegram.bot.web;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.methods.GetUserProfilePhotos;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.UserProfilePhotos;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.javazen.telegram.bot.datasource.model.SubjectCount;
import ru.javazen.telegram.bot.datasource.model.ChartData;
import ru.javazen.telegram.bot.datasource.model.TimeInterval;
import ru.javazen.telegram.bot.datasource.query.*;
import ru.javazen.telegram.bot.model.ChatType;
import ru.javazen.telegram.bot.repository.MessageRepository;
import ru.javazen.telegram.bot.util.ChartDataConverter;
import ru.javazen.telegram.bot.util.DateRange;
import ru.javazen.telegram.bot.util.DateRanges;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@Controller
@AllArgsConstructor
@PreAuthorize("hasAuthority('/admin')")
@RequestMapping("/admin")
public class AdminController {
    private static final TimeZone DEFAULT_TIME_ZONE = TimeZone.getTimeZone("GMT+04:00"); //TODO get actual tz from user

    private final DefaultAbsSender bot;
    private final MessageRepository messageRepository;
    private final CountEntitiesQuery countEntitiesQuery;
    private final TopEntitiesQuery topEntitiesQuery;
    private final IncomeOutcomeEntitiesQuery incomeOutcomeEntitiesQuery;
    private final ActiveEntitiesChartQuery activeEntitiesChartQuery;
    private final ChatTypesQuery chatTypesQuery;
    private final UserLanguagesQuery userLanguagesQuery;
    private final ChartDataConverter chartDataConverter;

    @GetMapping
    public String getChatView(Model model,
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
            dateRange = new DateRange(messageRepository.startBotDate(), new Date(), timeZone);
        }

        model.addAttribute("dateRange", dateRange);
        model.addAttribute("dateRangeName", range.name());

        User botUser = bot.getMe();
        model.addAttribute("bot", botUser);
        UserProfilePhotos photos = bot.execute(GetUserProfilePhotos.builder()
                .userId(botUser.getId()).limit(1).build());
        PhotoSize botPhoto = photos.getPhotos().stream().flatMap(List::stream).findAny().orElse(null);
        model.addAttribute("botPhoto", botPhoto);

        model.addAttribute("activityStatistics", List.of(
                Map.entry("Total", countEntitiesQuery.totalCount(dateRange)),
                Map.entry("Active", countEntitiesQuery.activeCount(dateRange)),
                Map.entry("Income", incomeOutcomeEntitiesQuery.incomeCount(dateRange)),
                Map.entry("Outcome", incomeOutcomeEntitiesQuery.outcomeCount(dateRange))
        ));

        model.addAttribute("topChats", topEntitiesQuery.topChats(dateRange, 20));
        model.addAttribute("topUsers", topEntitiesQuery.topUsers(dateRange, 20));

        return "admin";
    }

    @GetMapping("activity-per-entity-chart")
    @ResponseBody
    public ChartData getActivityPerEntityChart(@RequestParam(defaultValue = "1")
                                                       int intervalQuantity,
                                               @RequestParam(defaultValue = "DAY")
                                                       TimeInterval.Unit intervalUnit,
                                               @RequestParam
                                               @DateTimeFormat(pattern = "dd.MM.yyyy")
                                                       LocalDate from,
                                               @RequestParam
                                               @DateTimeFormat(pattern = "dd.MM.yyyy")
                                                       LocalDate to) {
        DateRange dateRange = new DateRange(from, to, DEFAULT_TIME_ZONE);
        TimeInterval timeInterval = new TimeInterval(intervalQuantity, intervalUnit);
        var statistics = activeEntitiesChartQuery.getActiveEntitiesChart(dateRange, timeInterval);
        return chartDataConverter.convert(statistics, DEFAULT_TIME_ZONE.toZoneId());
    }

    @GetMapping("chat-types")
    @ResponseBody
    public List<SubjectCount<ChatType>> getChatTypesChart(@RequestParam
                                                       @DateTimeFormat(pattern = "dd.MM.yyyy")
                                                               LocalDate from,
                                                          @RequestParam
                                                       @DateTimeFormat(pattern = "dd.MM.yyyy")
                                                               LocalDate to) {
        DateRange dateRange = new DateRange(from, to, DEFAULT_TIME_ZONE);
        return chatTypesQuery.getChatTypes(dateRange);
    }

    @GetMapping("user-languages")
    @ResponseBody
    public List<SubjectCount<String>> getUserLanguagesChart(@RequestParam
                                                         @DateTimeFormat(pattern = "dd.MM.yyyy")
                                                                 LocalDate from,
                                                            @RequestParam
                                                         @DateTimeFormat(pattern = "dd.MM.yyyy")
                                                                 LocalDate to) {
        DateRange dateRange = new DateRange(from, to, DEFAULT_TIME_ZONE);
        return userLanguagesQuery.getLanguages(dateRange);
    }
}
