package ru.javazen.telegram.bot.scheduler.parser;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.telegram.telegrambots.api.objects.Message;

import java.util.Date;

public interface ScheduledMessageParser {

    ParseResult parse(String text, Message message);

    boolean canParse(String text);

    @Getter
    @Setter
    @AllArgsConstructor
    class ParseResult {
        private Date date;
        private String message;
        private Integer repetitions;
        private String interval;
    }
}
