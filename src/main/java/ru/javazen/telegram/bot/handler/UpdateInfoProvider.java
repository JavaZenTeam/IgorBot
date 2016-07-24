package ru.javazen.telegram.bot.handler;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import ru.javazen.telegram.bot.Bot;
import ru.javazen.telegram.bot.entity.request.Update;
import ru.javazen.telegram.bot.entity.response.SendMessage;

import java.io.IOException;

import static ru.javazen.telegram.bot.Constants.PARSE_MODE_MARKDOWN;

public class UpdateInfoProvider implements UpdateHandler {

    @Autowired
    private ObjectMapper mapper;

    @Override
    public boolean handle(Update update, Bot bot) {
        try {
            SendMessage message = new SendMessage();
            message.setChatId(update.getMessage().getChat().getId());
            String value = mapper.writeValueAsString(update);
            message.setText("```" + value + "```");
            message.setParseMode(PARSE_MODE_MARKDOWN);
            bot.getService().sendMessage(message);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
