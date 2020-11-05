package ru.javazen.telegram.bot.preprocessor;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.telegram.telegrambots.meta.api.objects.Message;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Ignore
public class ReplaceDateTimeAdverbTest {
    private static final long SAMPLE_DATE = 1471460820;//17.08.2016 22:07:00

    @Test
    public void testReplaceNowToCurrentTime() throws Exception {
        ReplaceDateTimeAdverb instance = new ReplaceDateTimeAdverb("now", "dd.MM.yyyy HH:mm:ss");
        Message message = mock(Message.class);
        when(message.getDate()).thenReturn(Long.valueOf(SAMPLE_DATE).intValue());

        String r = instance.apply(message, " now ");
        System.out.println(r);
        Assert.assertEquals(" 17.08.2016 23:07:00 ", r);
    }

    @Test
    public void testReplaceTodayToCurrentDay() throws Exception {
        ReplaceDateTimeAdverb instance = new ReplaceDateTimeAdverb("today", "dd.MM.yy");
        Message message = mock(Message.class);
        when(message.getDate()).thenReturn(Long.valueOf(SAMPLE_DATE).intValue());

        String r = instance.apply(message, " today ");
        System.out.println(r);
        Assert.assertEquals(" 17.08.16 ", r);
    }

    @Test
    public void testReplaceTomorrowToNextDay() throws Exception {
        ReplaceDateTimeAdverb instance = new ReplaceDateTimeAdverb("tomorrow", "dd.MM.yy", 5, 1);
        Message message = mock(Message.class);
        when(message.getDate()).thenReturn(Long.valueOf(SAMPLE_DATE).intValue());

        String r = instance.apply(message, " tomorrow ");
        System.out.println(r);
        Assert.assertEquals(" 18.08.16 ", r);
    }

    @Test
    public void testReplaceYesterdayToPrevDay() throws Exception {
        ReplaceDateTimeAdverb instance = new ReplaceDateTimeAdverb("yesterday", "dd.MM.yy", 5, -1);
        Message message = mock(Message.class);
        when(message.getDate()).thenReturn(Long.valueOf(SAMPLE_DATE).intValue());

        String r = instance.apply(message, " yesterday ");
        System.out.println(r);
        Assert.assertEquals(" 16.08.16 ", r);
    }
}