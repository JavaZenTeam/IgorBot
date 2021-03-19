package ru.javazen.telegram.bot.web;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.methods.GetUserProfilePhotos;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.UserProfilePhotos;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.javazen.telegram.bot.datasource.StatisticDataSource;
import ru.javazen.telegram.bot.model.ChatEntity;
import ru.javazen.telegram.bot.model.UserEntity;
import ru.javazen.telegram.bot.repository.MessageRepository;
import ru.javazen.telegram.bot.util.DateRange;
import ru.javazen.telegram.bot.util.DateRanges;
import ru.javazen.telegram.bot.util.ActivityStatisticSummary;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

@Slf4j
@Controller
@AllArgsConstructor
@PreAuthorize("hasAuthority('/admin')")
@RequestMapping("/admin")
public class AdminController {
    private final DefaultAbsSender bot;
    private final MessageRepository messageRepository;
    private final StatisticDataSource<UserEntity> chatDataSource;
    private final StatisticDataSource<ChatEntity> userDataSource;

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
        UserProfilePhotos photos = bot.execute(new GetUserProfilePhotos().setUserId(botUser.getId()).setLimit(1));
        PhotoSize botPhoto = photos.getPhotos().stream().flatMap(List::stream).findAny().orElse(null);
        model.addAttribute("botPhoto", botPhoto);

        var userActivityStatistic = chatDataSource.topActivity(null, dateRange);
        model.addAttribute("userActivityStatisticSummary", new ActivityStatisticSummary(userActivityStatistic, 6));
        var chatActivityStatistic = userDataSource.topActivity(null, dateRange);
        model.addAttribute("chatActivityStatisticSummary", new ActivityStatisticSummary(chatActivityStatistic, 6));

        return "admin";
    }
}
