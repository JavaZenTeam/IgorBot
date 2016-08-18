package ru.javazen.telegram.bot.preprocessor;

import org.junit.Assert;
import org.junit.Test;
import ru.javazen.telegram.bot.entity.request.Message;
import ru.javazen.telegram.bot.entity.request.Update;

public class ReplaceDateTimeAdverbTest {
    private static final long SAMPLE_DATE = 1471460820;//17.08.2016 22:07:00

    @Test
    public void testReplaceNowToCurrentTime() throws Exception {
        ReplaceDateTimeAdverb instance = new ReplaceDateTimeAdverb("now", "dd.MM.yyyy HH:mm:ss");
        Update update = new Update();
        update.setMessage(new Message());
        update.getMessage().setDate(SAMPLE_DATE);

        String r = instance.apply(update, " now ");
        System.out.println(r);
        Assert.assertEquals(" 17.08.2016 22:07:00 ", r);
    }

    @Test
    public void testReplaceTodayToCurrentDay() throws Exception {
        ReplaceDateTimeAdverb instance = new ReplaceDateTimeAdverb("today", "dd.MM.yy");
        Update update = new Update();
        update.setMessage(new Message());
        update.getMessage().setDate(SAMPLE_DATE);

        String r = instance.apply(update, " today ");
        System.out.println(r);
        Assert.assertEquals(" 17.08.16 ", r);
    }

    @Test
    public void testReplaceTomorrowToNextDay() throws Exception {
        ReplaceDateTimeAdverb instance = new ReplaceDateTimeAdverb("tomorrow", "dd.MM.yy", 5, 1);
        Update update = new Update();
        update.setMessage(new Message());
        update.getMessage().setDate(SAMPLE_DATE);

        String r = instance.apply(update, " tomorrow ");
        System.out.println(r);
        Assert.assertEquals(" 18.08.16 ", r);
    }

    @Test
    public void testReplaceYesterdayToPrevDay() throws Exception {
        ReplaceDateTimeAdverb instance = new ReplaceDateTimeAdverb("yesterday", "dd.MM.yy", 5, -1);
        Update update = new Update();
        update.setMessage(new Message());
        update.getMessage().setDate(SAMPLE_DATE);

        String r = instance.apply(update, " yesterday ");
        System.out.println(r);
        Assert.assertEquals(" 16.08.16 ", r);
    }
}