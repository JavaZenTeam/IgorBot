package ru.javazen.telegram.bot.util;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.TimeZone;
import java.util.function.Function;

public enum DateRanges implements Function<TimeZone, DateRange> {
    LAST_DAY((timeZone) -> new DateRange(LocalDate.now(), LocalDate.now(), timeZone)),
    LAST_WEEK((timeZone) -> lastUnit(ChronoUnit.WEEKS, timeZone)),
    LAST_MONTH((timeZone) -> lastUnit(ChronoUnit.MONTHS, timeZone)),
    LAST_YEAR((timeZone) -> lastUnit(ChronoUnit.YEARS, timeZone)),
    ALL_TIME(),
    CUSTOM();

    private Function<TimeZone, DateRange> delegate;

    DateRanges(Function<TimeZone, DateRange> delegate) {
        this.delegate = delegate;
    }

    DateRanges() {
        this.delegate = (timeZone) -> null;
    }

    @Override
    public DateRange apply(TimeZone timeZone) {
        return delegate.apply(timeZone);
    }

    private static DateRange lastUnit(TemporalUnit unit, TimeZone timeZone) {
        LocalDate now = LocalDate.now();
        LocalDate from = now.minus(1, unit);
        return new DateRange(from, now, timeZone);
    }
}
