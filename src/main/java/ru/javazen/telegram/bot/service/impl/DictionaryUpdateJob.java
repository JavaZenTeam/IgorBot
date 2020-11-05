package ru.javazen.telegram.bot.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.javazen.telegram.bot.repository.DictionaryRepository;

@Component
@AllArgsConstructor
@Slf4j
@Profile("production")
public class DictionaryUpdateJob  {

    private DictionaryRepository dictionaryRepository;

    @Transactional
    @EventListener(ContextRefreshedEvent.class)
    @Scheduled(cron = "${dictionary.sync.cron}")
    public void sync() {
        log.debug("dictionary sync started");
        dictionaryRepository.truncate();
        dictionaryRepository.sync();
        log.debug("dictionary sync completed");
    }
}
