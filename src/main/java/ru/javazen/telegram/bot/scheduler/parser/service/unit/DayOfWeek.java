package ru.javazen.telegram.bot.scheduler.parser.service.unit;

public enum DayOfWeek {
    MONDAY(2),
    TUESDAY(3),
    WEDNESDAY(4),
    THURSDAY(5),
    FRIDAY(6),
    SATURDAY(7),
    SUNDAY(1);

    private int offset;

    DayOfWeek(int offset) {
        this.offset = offset;
    }

    public int getOffset() {
        return offset;
    }
}
