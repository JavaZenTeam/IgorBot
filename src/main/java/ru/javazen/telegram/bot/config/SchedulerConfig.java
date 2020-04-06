package ru.javazen.telegram.bot.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.javazen.telegram.bot.filter.RegexpFilter;
import ru.javazen.telegram.bot.handler.FilterAdapter;
import ru.javazen.telegram.bot.scheduler.SchedulerExtendNotifyHandler;
import ru.javazen.telegram.bot.scheduler.SchedulerNotifyHandler;
import ru.javazen.telegram.bot.scheduler.UnschedulerNotifyHandler;
import ru.javazen.telegram.bot.scheduler.parser.ShiftTimeParser;
import ru.javazen.telegram.bot.scheduler.parser.SpecificTimeParser;
import ru.javazen.telegram.bot.scheduler.service.MessageSchedulerService;
import ru.javazen.telegram.bot.service.ChatConfigService;

import java.util.Arrays;
import java.util.function.Supplier;

@Configuration
public class SchedulerConfig {

    public static final int DAYS_LIMIT = 3655;
    public static final int REPETITION_SECONDS_LIMIT = 60 * 60 - 1;
    public static final int MAX_REPETITION_UNDER_LIMIT = 50;

    @Bean("scheduler")
    public SchedulerNotifyHandler schedulerNotifyHandler(MessageSchedulerService messageSchedulerService,
                                                         @Qualifier("okSupplier") Supplier<String> okSupplier,
                                                         ShiftTimeParser shiftTimeParser,
                                                         SpecificTimeParser specificTimeParser,
                                                         ChatConfigService chatConfigService) {

        return new SchedulerNotifyHandler(
                messageSchedulerService,
                DAYS_LIMIT,
                okSupplier,
                Arrays.asList(shiftTimeParser, specificTimeParser),
                chatConfigService,
                REPETITION_SECONDS_LIMIT,
                MAX_REPETITION_UNDER_LIMIT);
    }

    @Bean("schedulerExtend")
    public SchedulerExtendNotifyHandler schedulerExtendNotifyHandler(MessageSchedulerService messageSchedulerService,
        @Qualifier("okSupplier") Supplier<String> okSupplier,
        @Qualifier("extendShiftTimeParser") ShiftTimeParser shiftTimeParser) {

        return new SchedulerExtendNotifyHandler(
                messageSchedulerService,
                DAYS_LIMIT,
                okSupplier,
                Arrays.asList(shiftTimeParser));
    }

    @Bean("unscheduler")
    public FilterAdapter unschedulerFilterAdapter(UnschedulerNotifyHandler unschedulerNotifyHandler) {
        RegexpFilter regexpFilter = new RegexpFilter();
        regexpFilter.setPattern("(з[ао]бу[дт]ь|ш[ао]+т[ао]+п)");
        return new FilterAdapter(regexpFilter, unschedulerNotifyHandler);
    }

    @Bean
    public UnschedulerNotifyHandler unschedulerNotifyHandler(MessageSchedulerService messageSchedulerService,
                                                             @Qualifier("sadOkSupplier") Supplier<String> okSupplier) {
        return new UnschedulerNotifyHandler(messageSchedulerService, okSupplier);
    }

    @Bean
    ShiftTimeParser shiftTimeParser(@Qualifier("defaultSupplier") Supplier<String> defaultMessageSupplier) {
        return new ShiftTimeParser(
                defaultMessageSupplier,
                "и+го+рь,\\s?ск[ао]ж[иы] че?р[еи]?з( .+)");
    }

    @Bean
    SpecificTimeParser specificTimeParser(
            @Qualifier("defaultSupplier") Supplier<String> defaultMessageSupplier,
            ChatConfigService chatConfigService) {
        return new SpecificTimeParser(
                defaultMessageSupplier,
                "и+го+рь,\\s?ск[ао]ж[иы]( .+)",
                chatConfigService);
    }

    @Bean
    ShiftTimeParser extendShiftTimeParser(@Qualifier("defaultSupplier") Supplier<String> defaultMessageSupplier) {
        return new ShiftTimeParser(
                defaultMessageSupplier,
                "(?:[ие](?:щ|сч)[еёо]|пр[оа]дли на|д[оа]бавь?)( .+)");
    }

    @Bean
    SpecificTimeParser extendSpecificTimeParser(
            @Qualifier("defaultSupplier") Supplier<String> defaultMessageSupplier,
            ChatConfigService chatConfigService) {
        return new SpecificTimeParser(
                defaultMessageSupplier,
                "п[еи]р[еи]в[еиоа]ди на( .+)",
                chatConfigService);
    }
}