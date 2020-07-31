package ru.javazen.telegram.bot.logging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.javazen.telegram.bot.IgorBotApplication;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Service to send messages to the Support chat of the bot.
 */
@Service
@Slf4j
public class TelegramLogger {

    private static final String ROOT_PACKAGE_NAME = IgorBotApplication.class.getPackage().getName();

    private final AbsSender sender;
    private final String supportChatId;

    public TelegramLogger(AbsSender sender, @Value("${bot.supportChat}") String supportChatId) {
        this.sender = sender;
        this.supportChatId = supportChatId;
    }

    /**
     * Log exception to support chat in the following format:
     * -- message
     * @param message - message to log
     */
    public void log(String message) {
        if (supportChatId.isEmpty()) {
            log.warn(String.format("Logging to support chat is disabled. Message: %s", message));
            return;
        }
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(supportChatId);
        sendMessage.setText(message);
        sendMessage.setParseMode("MARKDOWN");
        try {
            sender.execute(sendMessage);
        } catch (TelegramApiException te) {
            log.error("Can't send message to support chat", te);
        }
    }

    /**
     * Log exception to support chat in the following format:
     * -- message
     * -- exception with message
     * -- stacktrace
     * @param message - message to log
     * @param e - exception to log
     */
    public void log(String message, Exception e) {
        String stackTraceString = Arrays.stream(e.getStackTrace())
                .filter(el -> el.getClassName().startsWith(ROOT_PACKAGE_NAME))
                .map(StackTraceElement::toString)
                .collect(Collectors.joining("\n"));

        String text = String.format("%s\n%s\n```\n%s```", message, e, stackTraceString);

        this.log(text);
    }

    /**
     * Log exception to support chat in the following format:
     * Error!
     * -- exception with message
     * -- stacktrace
     * @param e - exception to log.
     */
    public void log(Exception e) {
        this.log("*Error!*", e);
    }
}
