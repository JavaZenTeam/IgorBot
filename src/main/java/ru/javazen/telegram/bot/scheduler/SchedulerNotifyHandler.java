package ru.javazen.telegram.bot.scheduler;

import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ru.javazen.telegram.bot.handler.UpdateHandler;
import ru.javazen.telegram.bot.model.MessageTask;
import ru.javazen.telegram.bot.scheduler.parser.ScheduledMessageParser;
import ru.javazen.telegram.bot.scheduler.service.MessageSchedulerService;
import ru.javazen.telegram.bot.util.MessageHelper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;
import java.util.function.Supplier;

public class SchedulerNotifyHandler implements UpdateHandler {

    private final MessageSchedulerService messageSchedulerService;
    private final int daysLimit;
    private final Supplier<String> successResponseSupplier;
    private final List<ScheduledMessageParser> scheduledMessageParsers;

    private DateFormat format = new SimpleDateFormat("HH:mm dd.MM.yy");
    {
        format.setTimeZone(TimeZone.getTimeZone("GMT+4:00"));
    }

    public SchedulerNotifyHandler(MessageSchedulerService messageSchedulerService,
                                  int daysLimit,
                                  Supplier<String> successResponseSupplier,
                                  List<ScheduledMessageParser> scheduledMessageParsers) {
        this.messageSchedulerService = messageSchedulerService;
        this.daysLimit = daysLimit;
        this.successResponseSupplier = successResponseSupplier;
        this.scheduledMessageParsers = scheduledMessageParsers;
    }

    @Override
    public boolean handle(Update update, AbsSender sender) throws TelegramApiException {
        String message = MessageHelper.getActualText(update.getMessage());
        if (message == null || message.isEmpty()) {
            return false;
        }

        final long userId = update.getMessage().getFrom().getId();


        final ScheduledMessageParser.ParseResult result = scheduledMessageParsers
                .stream()
                .filter(parser -> parser.canParse(message))
                .findAny()
                .map(parser -> parser.parse(message, update))
                .orElse(null);

        if (result == null) {
            return false;
        }


        Calendar calendar = new GregorianCalendar();
        calendar.add(Calendar.DAY_OF_YEAR, daysLimit);
        if (result.getDate().compareTo(calendar.getTime()) > 0) {
            sender.execute(MessageHelper.answer(update.getMessage(),
                    "Так долго я помнить не смогу, сорри", true));
            return true;
        }

        Calendar clarifyCalendar = Calendar.getInstance();
        clarifyCalendar.add(Calendar.HOUR_OF_DAY, 1);

        boolean needClarify = result.getDate().compareTo(clarifyCalendar.getTime()) > 0;

        sender.execute(MessageHelper.answer(update.getMessage(),
                successResponseSupplier.get() + (needClarify ? ", завел на " +
                format.format(result.getDate()): "")));

        MessageTask task = new MessageTask();
        task.setChatId(update.getMessage().getChat().getId());
        task.setMessageId(update.getMessage().getMessageId().longValue());
        task.setUserId(userId);
        task.setReplyMessageId(update.getMessage().getMessageId().longValue());
        task.setScheduledText(result.getMessage());
        task.setTimeOfCompletion(result.getDate().getTime());

        messageSchedulerService.scheduleTask(task);

        return true;
    }

}
