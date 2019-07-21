package ru.javazen.telegram.bot.util;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.function.Supplier;

public enum DateRanges implements Supplier<DateRange> {
    LAST_DAY(() -> lastUnit(ChronoUnit.DAYS)),
    LAST_WEEK(() -> lastUnit(ChronoUnit.WEEKS)),
    LAST_MONTH(() -> lastUnit(ChronoUnit.MONTHS)),
    LAST_YEAR(() -> lastUnit(ChronoUnit.YEARS)),
    ALL_TIME(),
    CUSTOM();

    private Supplier<DateRange> delegate;

    DateRanges(Supplier<DateRange> delegate) {
        this.delegate = delegate;
    }

    DateRanges() {
        this.delegate = () -> null;
    }

    @Override
    public DateRange get() {
        return delegate.get();
    }

    private static DateRange lastUnit(TemporalUnit unit) {
        LocalDate now = LocalDate.now();
        LocalDate from = now.minus(1, unit);
        return new DateRange(from, now);
    }
}
