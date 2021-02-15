package ru.javazen.telegram.bot.util;

import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.TimeZone;

@Getter
public class DateRange {
    private final LocalDate fromDate;
    private final LocalDate toDate;
    private final Date from;
    private final Date to;

    public DateRange(Date from, Date to, TimeZone timeZone) {
        this.from = from;
        this.to = to;
        this.fromDate = from.toInstant().atZone(timeZone.toZoneId()).toLocalDate();
        this.toDate = to.toInstant().atZone(timeZone.toZoneId()).toLocalDate();
    }

    public DateRange(LocalDate from, LocalDate to, TimeZone timeZone) {
        this.fromDate = from;
        this.toDate = to;
        ZonedDateTime fromDateTime = ZonedDateTime.of(from, LocalTime.MIN, timeZone.toZoneId());
        this.from = Date.from(fromDateTime.toInstant());
        ZonedDateTime toDateTime = ZonedDateTime.of(to, LocalTime.MAX, timeZone.toZoneId());
        this.to = Date.from(toDateTime.toInstant());
    }

    public long days() {
        return ChronoUnit.DAYS.between(fromDate, toDate) + 1;
    }
}
