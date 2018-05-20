package ru.javazen.telegram.bot.scheduler.parser.service.tructure;

import ru.javazen.telegram.bot.scheduler.parser.service.unit.DayOfWeek;
import ru.javazen.telegram.bot.scheduler.parser.service.unit.DayOffsets;
import ru.javazen.telegram.bot.scheduler.parser.service.unit.TimeOfDay;

import java.time.LocalTime;
import java.util.Date;

public class DateMessageStructure {

    //day element, position
    private TimeOfDay timeOfDay;
    private int timeOfDayPos = -1;

    private DayOffsets dayOffset;
    private int dayOffsetPos = -1;

    private DayOfWeek dayOfWeek;
    private int dayOfWeekPos = -1;

    private Date explicitDate;
    private LocalTime explicitTime;

    public TimeOfDay getTimeOfDay() {
        return timeOfDay;
    }

    public void setTimeOfDay(TimeOfDay timeOfDay) {
        this.timeOfDay = timeOfDay;
    }

    public int getTimeOfDayPos() {
        return timeOfDayPos;
    }

    public void setTimeOfDayPos(int timeOfDayPos) {
        this.timeOfDayPos = timeOfDayPos;
    }

    public DayOffsets getDayOffset() {
        return dayOffset;
    }

    public void setDayOffset(DayOffsets dayOffset) {
        this.dayOffset = dayOffset;
    }

    public int getDayOffsetPos() {
        return dayOffsetPos;
    }

    public void setDayOffsetPos(int dayOffsetPos) {
        this.dayOffsetPos = dayOffsetPos;
    }

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public int getDayOfWeekPos() {
        return dayOfWeekPos;
    }

    public void setDayOfWeekPos(int dayOfWeekPos) {
        this.dayOfWeekPos = dayOfWeekPos;
    }

    public Date getExplicitDate() {
        return explicitDate;
    }

    public void setExplicitDate(Date explicitDate) {
        this.explicitDate = explicitDate;
    }

    public LocalTime getExplicitTime() {
        return explicitTime;
    }

    public void setExplicitTime(LocalTime explicitTime) {
        this.explicitTime = explicitTime;
    }
}
