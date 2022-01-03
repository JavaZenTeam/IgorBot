package ru.javazen.telegram.bot.logging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.javazen.telegram.bot.BotUsageLogWrapper;
import ru.javazen.telegram.bot.IgorBotApplication;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service to send messages to the Support chat of the bot.
 */
@Service
@Slf4j
public class TelegramLogger {

    private static final String ROOT_PACKAGE_NAME = IgorBotApplication.class.getPackage().getName();

    private AbsSender sender;
    private final String supportChatId;

    public TelegramLogger(@Value("${bot.supportChat}") String supportChatId) {
        this.supportChatId = supportChatId;
    }

    @Autowired
    @Lazy
    public void setSender(AbsSender sender) {
        this.sender = sender;
    }

    /**
     * Log exception to support chat in the following format:
     * -- message
     *
     * @param message - message to log
     */
    public void log(String message) {
        if (supportChatId.isEmpty()) {
            log.warn(String.format("Logging to support chat is disabled. Message: %s", message));
            return;
        }
        SendMessage sendMessage = SendMessage.builder()
                .chatId(supportChatId)
                .text(message)
                .parseMode(ParseMode.MARKDOWN)
                .build();
        try {
            sender.execute(sendMessage);
        } catch (TelegramApiException te) {
            log.error("Can't send message to support chat", te);
        }
    }

    /**
     * Log exception to support chat in the following format:
     * -- Error!
     * -- exception with message
     * -- stacktrace
     * -- context variables
     *
     * @param context - context to log
     * @param e       - exception to log
     */
    public void log(Map<String, Object> context, Exception e) {
        String contextString = context.entrySet().stream()
                .map(entry -> String.format("*%s:* %s", entry.getKey(), entry.getValue()))
                .collect(Collectors.joining("\n"));

        String stackTraceString = Arrays.stream(e.getStackTrace())
                .filter(el -> el.getClassName().startsWith(ROOT_PACKAGE_NAME))
                .filter(el -> !el.getClassName().equals(BotUsageLogWrapper.class.getName()))
                .map(StackTraceElement::toString)
                .map(str -> str.replace(ROOT_PACKAGE_NAME, "~"))
                .collect(Collectors.joining("\n"));

        String text = String.join("\n",
                "*Error!*",
                e.toString(),
                "```",
                stackTraceString,
                "```",
                contextString
        );

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
        this.log(Collections.emptyMap(), e);
    }
}
