package ru.javazen.telegram.bot.util;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Date;

public class DateRange {
    private final Date from;
    private final Date to;

    public DateRange(Date from, Date to) {
        this.from = from;
        this.to = to;
    }


    public DateRange(LocalDate from, LocalDate to) {
        this.from = Date.from(from.atTime(0, 0, 0).toInstant(ZoneOffset.UTC));
        this.to = Date.from(to.atTime(23, 59, 59).toInstant(ZoneOffset.UTC));
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
