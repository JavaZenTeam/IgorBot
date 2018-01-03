package ru.javazen.telegram.bot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.javazen.telegram.bot.entity.Update;
import ru.javazen.telegram.bot.handler.UpdateHandler;
import ru.javazen.telegram.bot.method.send.SendMessage;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

public class CompositeBot extends AbsTelegramBot {
    private static final Logger LOGGER = LoggerFactory.getLogger(CompositeBot.class);
    private static final String ROOT_PACKAGE_NAME = CompositeBot.class.getPackage().getName();

    private Collection<UpdateHandler> updateHandlers = new ArrayList<>();
    private Long supportChatId;

    public CompositeBot(String name, TelegramService telegramService, String token) {
        super(name, telegramService, token);
    }

    public void setUpdateHandlers(Collection<UpdateHandler> updateHandlers) {
        this.updateHandlers.clear();
        this.updateHandlers.addAll(updateHandlers);
    }

    public void setSupportChatId(Long supportChatId) {
        this.supportChatId = supportChatId;
    }

    @PostConstruct
    public void onStart() {
        if (supportChatId != null){
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(supportChatId.toString());
            sendMessage.setText("ПРЕКЛОНИТЕСЬ ПРЕДО МНОЙ");
            getBotMethodExecutor().execute(sendMessage, Void.class);
        }
    }

    @Override
    public void handleUpdate(Update update) {

        if(update.getMessage() == null) { return; }
        try {
            for (UpdateHandler handler : updateHandlers){
                if (handler.handle(update, getBotMethodExecutor())) return;
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
                getBotMethodExecutor().execute(sendMessage, Void.class);
            }
        }
    }
}
