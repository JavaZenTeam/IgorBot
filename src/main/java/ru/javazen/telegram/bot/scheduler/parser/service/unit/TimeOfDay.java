package ru.javazen.telegram.bot.scheduler.parser.service.unit;

public enum TimeOfDay {
    MORNING(7),
    DAY(12),
    EVENING(17),
    NIGHT(21);

    private int time;

    TimeOfDay(int time) {
        this.time = time;
    }

    public int getTime() {
        return time;
    }
}
