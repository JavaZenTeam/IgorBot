package ru.javazen.telegram.bot.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.javazen.telegram.bot.CompositeBot;
import ru.javazen.telegram.bot.repository.MessageTaskRepository;
import ru.javazen.telegram.bot.scheduler.SchedulerNotifyHandler;
import ru.javazen.telegram.bot.scheduler.parser.ShiftTimeParser;
import ru.javazen.telegram.bot.scheduler.parser.SpecificTimeParser;
import ru.javazen.telegram.bot.scheduler.service.MessageSchedulerService;
import ru.javazen.telegram.bot.scheduler.service.MessageSchedulerServiceImpl;

import java.util.Arrays;
import java.util.function.Supplier;

@Configuration
public class SchedulerConfig {

    @Bean("scheduler")
    public SchedulerNotifyHandler schedulerNotifyHandler(MessageSchedulerService messageSchedulerService,
                                                         @Qualifier("okSupplier") Supplier<String> okSupplier,
                                                         ShiftTimeParser shiftTimeParser,
                                                         SpecificTimeParser specificTimeParser) {

        return new SchedulerNotifyHandler(
                messageSchedulerService,
                1827,
                okSupplier,
                Arrays.asList(shiftTimeParser, specificTimeParser));
    }

    @Bean
    public MessageSchedulerService messageSchedulerService(CompositeBot compositeBot,
                                                           MessageTaskRepository messageTaskRepository) {
        return new MessageSchedulerServiceImpl(compositeBot, messageTaskRepository);
    }

    @Bean
    ShiftTimeParser shiftTimeParser(@Qualifier("defaultSupplier") Supplier<String> defaultMessageSupplier) {
        return new ShiftTimeParser(
                defaultMessageSupplier,
                "и+го+рь,\\s?ск[ао]ж[иы] че?р[еи]?з( .+)");
    }


    @Bean
    SpecificTimeParser specificTimeParser(@Qualifier("defaultSupplier") Supplier<String> defaultMessageSupplier) {
        return new SpecificTimeParser(
                defaultMessageSupplier,
                "и+го+рь,\\s?ск[ао]ж[иы]( .+)");
    }
}