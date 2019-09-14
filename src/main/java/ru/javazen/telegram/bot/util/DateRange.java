package ru.javazen.telegram.bot.util;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.TimeZone;

public class DateRange {
    private final Date from;
    private final Date to;

    public DateRange(Date from, Date to) {
        this.from = from;
        this.to = to;
    }

    public DateRange(LocalDate from, LocalDate to, TimeZone timeZone) {
        ZonedDateTime fromDateTime = ZonedDateTime.of(from, LocalTime.MIN, timeZone.toZoneId());
        this.from = Date.from(fromDateTime.toInstant());
        ZonedDateTime toDateTime = ZonedDateTime.of(to, LocalTime.MAX, timeZone.toZoneId());
        this.to = Date.from(toDateTime.toInstant());
    }

    public Date getFrom() {
        return from;
    }

    public Date getTo() {
        return to;
    }
}
