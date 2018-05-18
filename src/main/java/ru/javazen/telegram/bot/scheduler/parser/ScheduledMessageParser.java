package ru.javazen.telegram.bot.scheduler.parser;

import lombok.Getter;
import lombok.Setter;
import org.telegram.telegrambots.api.objects.Update;

import java.util.Date;

public interface ScheduledMessageParser {

    ParseResult parse(String message, Update update);

    boolean canParse(String message);

    @Getter
    @Setter
    class ParseResult {
        private Date date;
        private String message;
    }
}
