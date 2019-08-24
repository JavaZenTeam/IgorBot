package ru.javazen.telegram.bot.scheduler;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ru.javazen.telegram.bot.handler.base.TextMessageHandler;
import ru.javazen.telegram.bot.model.MessageTask;
import ru.javazen.telegram.bot.scheduler.parser.ScheduledMessageParser;
import ru.javazen.telegram.bot.scheduler.service.MessageSchedulerService;
import ru.javazen.telegram.bot.service.ChatConfigService;
import ru.javazen.telegram.bot.util.DateInterval;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;
import java.util.function.Supplier;

public class SchedulerNotifyHandler implements TextMessageHandler {

    private static final String TIMEZONE_OFFSET_CONFIG_KEY = "TIMEZONE_OFFSET";

    private final MessageSchedulerService messageSchedulerService;
    private final int daysLimit;
    private final Supplier<String> successResponseSupplier;
    private final List<ScheduledMessageParser> scheduledMessageParsers;
    private ChatConfigService chatConfigService;
    private final int repetitionSecondsLimit;
    private final int repetitionMaxTimesUnderLimit;

    //private DateFormat format = new SimpleDateFormat("HH:mm dd.MM.yy");
    //TimeZone timeZone = TimeZone.getTimeZone(chatConfigService.getProperty(
    //        update.getMessage().getFrom().getId(),
    //        TIMEZONE_OFFSET_CONFIG_KEY).orElse("UTC+04:00"));
    //format.setTimeZone(TimeZone.getTimeZone("GMT+4:00"));


    public SchedulerNotifyHandler(MessageSchedulerService messageSchedulerService,
                                  int daysLimit,
                                  Supplier<String> successResponseSupplier,
                                  List<ScheduledMessageParser> scheduledMessageParsers,
                                  ChatConfigService chatConfigService,
                                  int repetitionSecondsLimit,
                                  int repetitionMaxTimesUnderLimit) {
        this.messageSchedulerService = messageSchedulerService;
        this.daysLimit = daysLimit;
        this.successResponseSupplier = successResponseSupplier;
        this.scheduledMessageParsers = scheduledMessageParsers;
        this.chatConfigService = chatConfigService;
        this.repetitionSecondsLimit = repetitionSecondsLimit;
        this.repetitionMaxTimesUnderLimit = repetitionMaxTimesUnderLimit;
    }

    @Override
    public boolean handle(Message message, String text, AbsSender sender) throws TelegramApiException {
        final long userId = message.getFrom().getId();


        final ScheduledMessageParser.ParseResult result = scheduledMessageParsers
                .stream()
                .filter(parser -> parser.canParse(text))
                .findAny()
                .map(parser -> parser.parse(text, message))
                .orElse(null);

        if (result == null) {
            return false;
        }


        Calendar calendar = new GregorianCalendar();
        calendar.add(Calendar.DAY_OF_YEAR, daysLimit);
        if (result.getDate().compareTo(calendar.getTime()) > 0) {
            sender.execute(new SendMessage(message.getChatId(), "Так долго я помнить не смогу, сорри"));
            return true;
        }

        calendar.setTime(result.getDate());
        calendar.add(Calendar.SECOND, repetitionSecondsLimit);
        if (result.getRepetitions() != null &&
                (result.getRepetitions() < 0 || result.getRepetitions() > repetitionMaxTimesUnderLimit) &&
                DateInterval.apply(result.getInterval(), result.getDate()).compareTo(calendar.getTime()) < 0) {
            sender.execute(new SendMessage(message.getChatId(), "Не, я устану повторять так часто"));
            return true;
        }

        Calendar clarifyCalendar = Calendar.getInstance();
        clarifyCalendar.add(Calendar.HOUR_OF_DAY, 1);

        boolean needClarify = result.getDate().compareTo(clarifyCalendar.getTime()) > 0;

        DateFormat format = new SimpleDateFormat("HH:mm dd.MM.yy");
        TimeZone timeZone = TimeZone.getTimeZone("GMT" + chatConfigService.getProperty(
                message.getFrom().getId(),
                TIMEZONE_OFFSET_CONFIG_KEY).orElse("+04:00"));

        format.setTimeZone(timeZone);

        sender.execute(new SendMessage(message.getChatId(),
                successResponseSupplier.get() + (needClarify ? ", завел на " +
                format.format(result.getDate()): "")));

        MessageTask task = new MessageTask();
        task.setChatId(message.getChatId());
        task.setMessageId(message.getMessageId().longValue());
        task.setUserId(userId);
        task.setReplyMessageId(message.getMessageId().longValue());
        task.setScheduledText(result.getMessage());
        task.setTimeOfCompletion(result.getDate().getTime());
        task.setRepeatCount(result.getRepetitions());
        task.setRepeatInterval(result.getInterval());

        messageSchedulerService.scheduleTask(task);

        return true;
    }

}
