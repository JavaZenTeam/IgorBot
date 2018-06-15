package ru.javazen.telegram.bot.scheduler.parser;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.telegram.telegrambots.api.objects.Update;

import java.time.Instant;

public interface ScheduledMessageParser {

    ParseResult parse(String message, Update update);

    boolean canParse(String message);

    @Getter
    @Setter
    @AllArgsConstructor
    class ParseResult {
        private Instant date;
        private String message;
    }
}
