package ru.javazen.telegram.bot.datasource.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.text.DateFormatSymbols;
import java.util.Locale;
import java.util.function.BiFunction;
import java.util.function.Function;

@AllArgsConstructor
@Getter
public class TimeGroup {
    private final int quantity;
    private final Field field;

    public Function<Integer, String> getNameFunc() {
        if (quantity == 1) {
            return field.getNameFunc();
        }

        var nameBiFunc = field.getNameBiFunc();
        return (i) -> {
            String start = nameBiFunc.apply(i, false);
            String end = nameBiFunc.apply(i + quantity - 1, true);
            return start + "-" + end;
        };
    }

    @RequiredArgsConstructor
    @Getter
    public enum Field {
        QUARTER(1, 4, i -> "Q" + i),
        MONTH(1, 12, Field::getMonthString),
        DAY(1, 30, Object::toString),
        DOW(0, 6, Field::getWeekdayString),
        HOUR(0, 23, (i, b) -> String.format("%02d:%02d", i, b ? 59 : 0)),
        ;

        private final int start;
        private final int stop;
        private final BiFunction<Integer, Boolean, String> nameBiFunc;

        Field(int start, int stop, Function<Integer, String> nameFunc) {
            this.start = start;
            this.stop = stop;
            this.nameBiFunc = (i, b) -> nameFunc.apply(i);
        }

        private final static String[] months = DateFormatSymbols.getInstance(Locale.ENGLISH).getShortMonths();
        private final static String[] weekdays = DateFormatSymbols.getInstance(Locale.ENGLISH).getShortWeekdays();

        private static String getMonthString(int month) {
            return months[month - 1];
        }

        private static String getWeekdayString(int day) {
            return weekdays[day + 1];
        }

        public Function<Integer, String> getNameFunc() {
            return (i) -> nameBiFunc.apply(i, false);
        }
    }
}
