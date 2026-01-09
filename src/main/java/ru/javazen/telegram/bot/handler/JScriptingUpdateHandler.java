package ru.javazen.telegram.bot.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.javazen.telegram.bot.handler.base.TextMessageHandler;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.HashMap;
import java.util.Map;

public class JScriptingUpdateHandler implements TextMessageHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(JScriptingUpdateHandler.class);

    private Map<Long, String> handlersByChat = new HashMap<>();
    private int admin;
    private String scriptingProvider;

    private static final String JS_WRAPPER_EXAMPLE =
            "var handler = function(update, executor) {" +
                    "  var helper = Java.type('ru.javazen.telegram.bot.util.MessageHelper');" +
                    "  var say = function(text) { if (text != null) executor.execute(helper.answer(update.getMessage(), text), java.lang.Void.class); };" +
                    "  var reply = function(text) { if (text != null) executor.execute(helper.answer(update.getMessage(), text, true), java.lang.Void.class); };" +
                    "  var msg = helper.getActualText(update.getMessage());" +
                    "  {script}" +
                    "}";

    @Override
    public boolean handle(Message message, String text, AbsSender sender) throws TelegramApiException {
        if (text.startsWith("/script") && message.getFrom().getId().equals(admin)) {

            String script = text.replace("/script", "");

            script = JS_WRAPPER_EXAMPLE.replace("{script}", script);
            LOGGER.debug("For {} chat add following script: {}", message.getChat(), script);

            handlersByChat.put(message.getChatId(), script);
        }
        if (handlersByChat.containsKey(message.getChatId())) {

            String script = handlersByChat.get(message.getChatId());
            LOGGER.debug("execute {} for {}", script, message.getChat());

            ScriptEngineManager factory = new ScriptEngineManager();
            ScriptEngine engine = factory.getEngineByName(scriptingProvider);
            try {
                engine.eval(script);
                Invocable invocable = (Invocable) engine;

                Object result = invocable.invokeFunction("handler", message, sender);

                if (result != null && result instanceof Boolean) {
                    return (Boolean) result;
                }
            } catch (ScriptException | NoSuchMethodException e) {
                LOGGER.error("JS HAS FALLEN", e);
                SendMessage sendMessage = new SendMessage(message.getChatId().toString(), e.getMessage());
                sendMessage.setMessageThreadId(message.getMessageThreadId());
                sender.execute(sendMessage);
            }
        }
        return false;
    }

    public void setScriptingProvider(String scriptingProvider) {
        this.scriptingProvider = scriptingProvider;
    }

    public void setAdmin(int admin) {
        this.admin = admin;
    }
}
