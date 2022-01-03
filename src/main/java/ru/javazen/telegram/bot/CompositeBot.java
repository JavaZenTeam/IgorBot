package ru.javazen.telegram.bot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.groupadministration.LeaveChat;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.meta.generics.BotSession;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.javazen.telegram.bot.handler.base.UpdateHandler;
import ru.javazen.telegram.bot.logging.TelegramLogger;
import ru.javazen.telegram.bot.service.MessageCollectorService;
import ru.javazen.telegram.bot.util.UpdateHelper;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

@Slf4j
@Service
public class CompositeBot extends TelegramLongPollingBot {

    private final String name;
    private final String token;

    private final Supplier<String> startMessageSupplier;
    private final MessageCollectorService messageCollectorService;

    private TelegramLogger tgLogger;

    private final Collection<UpdateHandler> updateHandlers = new ArrayList<>();

    private final BotUsageLogWrapper wrapper = new BotUsageLogWrapper(this);
    private BotSession session;

    @Autowired
    public CompositeBot(@Value("${bot.name}") String name,
                        @Value("${bot.token}") String token,
                        @Qualifier("startSupplier") Supplier<String> startMessageSupplier,
                        MessageCollectorService messageCollectorService,
                        DefaultBotOptions options) {
        super(options);
        this.name = name;
        this.token = token;

        this.startMessageSupplier = startMessageSupplier;
        this.messageCollectorService = messageCollectorService;
    }

    @Autowired
    public void setUpdateHandlers(@Qualifier("updateHandlers") Collection<UpdateHandler> updateHandlers) {
        this.updateHandlers.clear();
        this.updateHandlers.addAll(updateHandlers);
    }

    @Autowired
    public void setTgLogger(TelegramLogger telegramLogger) {
        this.tgLogger = telegramLogger;
    }

    @Override
    public String getBotUsername() {
        return name;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            Message sentMessage = null;
            String handlerName = null;
            Optional<Chat> chat = UpdateHelper.tryResolveChat(update);

            for (UpdateHandler handler : updateHandlers) {
                try {
                    boolean handled = handler.handle(update, wrapper);

                    if (handled) {
                        sentMessage = wrapper.getSentMessage();
                        handlerName = handler.getName();
                        log.debug("Update {} handled by {}", update, handlerName);
                        break;
                    }
                } catch (TelegramApiRequestException e) {
                    if (e.getApiResponse().contains("have no rights") && chat.isPresent()) {
                        tryLeaveChat(chat.get());
                    } else {
                        log(update, handler, chat.orElse(null), e);
                    }
                } catch (Exception e) {
                    log(update, handler, chat.orElse(null), e);
                }
            }

            messageCollectorService.saveUpdate(update);
            if (sentMessage != null) {
                messageCollectorService.saveBotUsage(update, sentMessage, handlerName);
            }

        } catch (Exception e) {
            log.error("Error on handling update {}", update, e);
            tgLogger.log(e);
        }
    }

    private void log(Update update, UpdateHandler handler, Chat chat, Exception e) {
        if (handler != null) {
            log.error("Error on handling update {}; handler={}", update, handler, e);
        } else {
            log.error("Error on handling update {}", update, e);
        }
        Map<String, Object> context = new LinkedHashMap<>();
        if (chat != null) {
            context.put("chat.id", chat.getId());
        }
        if (handler != null) {
            context.put("handler", handler.getName());
        }
        tgLogger.log(context, e);
    }

    private void tryLeaveChat(Chat chat) {
        if (chat.isUserChat()) {
            log.debug("Can't leave user chat (@{})", chat.getUserName());
        } else {
            try {
                execute(new LeaveChat(String.valueOf(chat.getId())));
            } catch (TelegramApiException e) {
                log.error("Error on leave chat {}", chat, e);
            }
        }
    }

    @PostConstruct
    public void onStart() throws TelegramApiException {
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        session = botsApi.registerBot(this);

        log.info("{}' bot started.", name);
        tgLogger.setSender(this);
        tgLogger.log(startMessageSupplier.get());
    }

    @PreDestroy
    public void onStop() {
        if (session != null) {
            session.stop();
        }
        log.info("'{}' bot stopped.", name);
    }
}
