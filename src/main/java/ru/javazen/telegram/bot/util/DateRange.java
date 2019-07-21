package ru.javazen.telegram.bot.util;

import java.time.*;
import java.util.Date;

public class DateRange {
    private final Date from;
    private final Date to;

    public DateRange(Date from, Date to) {
        this.from = from;
        this.to = to;
    }


    public DateRange(LocalDate from, LocalDate to) {
        ZonedDateTime fromDateTime = ZonedDateTime.of(from, LocalTime.MIN, ZoneId.systemDefault());
        this.from = Date.from(fromDateTime.toInstant());
        ZonedDateTime toDateTime = ZonedDateTime.of(to, LocalTime.MAX, ZoneId.systemDefault());
        this.to = Date.from(toDateTime.toInstant());
    }

    public Date getFrom() {
        return from;
    }

    public Date getTo() {
        return to;
    }

    public Duration duration() {
        return Duration.between(Instant.ofEpochMilli(from.getTime()), Instant.ofEpochMilli(to.getTime()));
    }
}
