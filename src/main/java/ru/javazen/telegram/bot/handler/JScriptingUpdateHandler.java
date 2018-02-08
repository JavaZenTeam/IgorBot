package ru.javazen.telegram.bot.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ru.javazen.telegram.bot.util.MessageHelper;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.HashMap;
import java.util.Map;

public class JScriptingUpdateHandler implements UpdateHandler {

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
    public boolean handle(Update update, AbsSender sender) throws TelegramApiException {
        String message = update.getMessage().getText();
        if (message != null
                && message.startsWith("/script")
                && update.getMessage().getFrom().getId().equals(admin)) {

            String script = message.replace("/script", "");

            script = JS_WRAPPER_EXAMPLE.replace("{script}", script);
            LOGGER.debug("For {} chat add following script: {}",
                    update.getMessage().getChat(),
                    script);

            handlersByChat.put(update.getMessage().getChat().getId(), script);
        }
        if (handlersByChat.containsKey(update.getMessage().getChat().getId())) {

            String script = handlersByChat.get(update.getMessage().getChat().getId());
            LOGGER.debug("execute {} for {}", script, update.getMessage().getChat());

            ScriptEngineManager factory = new ScriptEngineManager();
            ScriptEngine engine = factory.getEngineByName(scriptingProvider);
            try {
                engine.eval(script);
                Invocable invocable = (Invocable) engine;

                Object result = invocable.invokeFunction("handler", update, sender);

                if (result != null && result instanceof Boolean) {
                    return (Boolean) result;
                }
            } catch (ScriptException | NoSuchMethodException e) {
                LOGGER.error("JS HAS FALLEN", e);
                sender.execute(MessageHelper.answer(update.getMessage(), e.getMessage()));
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
