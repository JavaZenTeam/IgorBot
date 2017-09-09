package ru.javazen.telegram.bot.handler;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import ru.javazen.telegram.bot.BotMethodExecutor;
import ru.javazen.telegram.bot.entity.Update;
import ru.javazen.telegram.bot.method.send.SendMessage;

import java.io.IOException;

public class UpdateInfoProvider implements UpdateHandler {

    @Autowired
    private ObjectMapper mapper;

    @Override
    public boolean handle(Update update, BotMethodExecutor executor) {
        try {
            SendMessage message = new SendMessage();
            message.setChatId(update.getMessage().getChat().getId().toString());
            String value = mapper.writeValueAsString(update);
            message.setText("```" + value + "```");
            message.setParseMode("MARKDOWN");
            executor.execute(message, Void.class);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
