package ru.javazen.telegram.bot.stats;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;
import ru.javazen.telegram.bot.repository.DailyUserChatStatisticRepository;

import java.time.Duration;

@Component
@Profile("production")
@Slf4j
@RequiredArgsConstructor
public class DailyStatisticViewRefreshJob {
    private final DailyUserChatStatisticRepository repository;

    @Scheduled(cron = "0 0 * * * *") //every hour
    @EventListener(ContextRefreshedEvent.class)
    @Transactional
    public void refresh() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        repository.refreshView();

        stopWatch.stop();

        Duration duration = Duration.ofMillis(stopWatch.getTotalTimeMillis());
        if (duration.toMinutes() > 0) {
            log.warn("DailyStatisticView refreshed in {}", duration);
        } else {
            log.debug("DailyStatisticView refreshed in {}", duration);
        }
    }
}
