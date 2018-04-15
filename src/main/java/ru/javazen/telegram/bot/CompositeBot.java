package ru.javazen.telegram.bot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.generics.BotSession;
import ru.javazen.telegram.bot.handler.UpdateHandler;
import ru.javazen.telegram.bot.repository.BotUsageLogRepository;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class CompositeBot extends TelegramLongPollingBot {
    private static final Logger LOGGER = LoggerFactory.getLogger(CompositeBot.class);
    private static final String ROOT_PACKAGE_NAME = CompositeBot.class.getPackage().getName();

    private Collection<UpdateHandler> updateHandlers = new ArrayList<>();
    private Long supportChatId;
    private Supplier<String> startMessageSupplier;

    private String name;
    private String token;
    private BotSession session;

    private BotUsageLogRepository logRepository;

    public CompositeBot(String name, String token) {
        this.name = name;
        this.token = token;
    }

    public void setUpdateHandlers(Collection<UpdateHandler> updateHandlers) {
        this.updateHandlers.clear();
        this.updateHandlers.addAll(updateHandlers);
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
        if(update.getMessage() == null) { return; }
        try {
            for (UpdateHandler handler : updateHandlers){
                BotUsageLogWrapper wrapper = new BotUsageLogWrapper();
                wrapper.setBotUsageLogRepository(logRepository);
                wrapper.setModuleName(handler.getName());
                wrapper.setSourceMessage(update.getMessage());
                wrapper.setTarget(this);

                if (handler.handle(update, wrapper)) return;
            }
            LOGGER.debug("This update is not handled: {}", update);
        } catch (Exception e){
            LOGGER.error("Error on handling update", e);
            if (supportChatId != null){
                String stackTraceString = Arrays.stream(e.getStackTrace())
                        .filter(el -> el.getClassName().startsWith(ROOT_PACKAGE_NAME))
                        .map(StackTraceElement::toString)
                        .collect(Collectors.joining("\n"));

                SendMessage sendMessage = new SendMessage();
                sendMessage.setChatId(supportChatId.toString());

                String message = String.format("*Error!*\n%s\n```\n%s```", e, stackTraceString);
                sendMessage.setText(message);
                sendMessage.setParseMode("MARKDOWN");
                try {
                    execute(sendMessage);
                } catch (TelegramApiException te) {
                    LOGGER.error("Can't send message to support chat", te);
                }

            }
        }
    }

    public void setSupportChatId(Long supportChatId) {
        this.supportChatId = supportChatId;
    }

    public void setStartMessageSupplier(Supplier<String> startMessageSupplier) {
        this.startMessageSupplier = startMessageSupplier;
    }

    @Autowired
    public void setLogRepository(BotUsageLogRepository logRepository) {
        this.logRepository = logRepository;
    }

    static {
        ApiContextInitializer.init();
    }

    @PostConstruct
    public void onStart() throws TelegramApiException {
        TelegramBotsApi botsApi = new TelegramBotsApi();
        session = botsApi.registerBot(this);

        if (supportChatId != null && startMessageSupplier != null){

            SendMessage message = new SendMessage()
                    .setChatId(supportChatId)
                    .setText(startMessageSupplier.get());

            execute(message);
        }
    }

    @PreDestroy
    public void onStop() {
        if (session != null) {
            session.stop();
        }
    }
}
