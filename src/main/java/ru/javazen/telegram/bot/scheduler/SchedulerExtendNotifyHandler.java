package ru.javazen.telegram.bot.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.javazen.telegram.bot.handler.base.TextMessageHandler;
import ru.javazen.telegram.bot.scheduler.parser.ScheduledMessageParser;
import ru.javazen.telegram.bot.scheduler.service.MessageSchedulerService;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.function.Supplier;

@Slf4j
public class SchedulerExtendNotifyHandler implements TextMessageHandler {

    private final MessageSchedulerService messageSchedulerService;
    private final int daysLimit;
    private final Supplier<String> successResponseSupplier;
    private final List<ScheduledMessageParser> scheduledMessageParsers;

    public SchedulerExtendNotifyHandler(MessageSchedulerService messageSchedulerService,
                                  int daysLimit,
                                  Supplier<String> successResponseSupplier,
                                  List<ScheduledMessageParser> scheduledMessageParsers) {
        this.messageSchedulerService = messageSchedulerService;
        this.daysLimit = daysLimit;
        this.successResponseSupplier = successResponseSupplier;
        this.scheduledMessageParsers = scheduledMessageParsers;
    }

    @Override
    public boolean handle(Message message, String text, AbsSender sender) throws TelegramApiException {
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
            sender.execute(new SendMessage(message.getChatId().toString(), "Так долго я помнить не смогу, сорри"));
            return true;
        }

        long additionalTime = result.getDate().getTime() - new Date().getTime();
        if (message.getReplyToMessage() != null && messageSchedulerService.extendTaskByChatAndMessage(
                message.getChatId(), message.getReplyToMessage().getMessageId(), additionalTime)) {
            sender.execute(new SendMessage(message.getChatId().toString(), successResponseSupplier.get()));
            log.debug("Task extended");
            return true;
        }

        return false;
    }
}
