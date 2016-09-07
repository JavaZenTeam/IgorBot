package ru.javazen.telegram.bot.handler;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import ru.javazen.telegram.bot.Bot;
import ru.javazen.telegram.bot.entity.request.Update;
import ru.javazen.telegram.bot.entity.response.ParseMode;
import ru.javazen.telegram.bot.entity.response.SendMessage;
import ru.javazen.telegram.bot.service.TelegramBotService;

import java.io.IOException;

public class UpdateInfoProvider implements UpdateHandler {

    @Autowired
    private TelegramBotService botService;

    @Autowired
    private ObjectMapper mapper;

    @Override
    public boolean handle(Update update) {
        try {
            SendMessage message = new SendMessage();
            message.setChatId(update.getMessage().getChat().getId());
            String value = mapper.writeValueAsString(update);
            message.setText("```" + value + "```");
            message.setParseMode(ParseMode.MARKDOWN);
            botService.sendMessage(message);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
