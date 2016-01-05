package ru.javazen.telegram.bot;

import ru.javazen.telegram.bot.entity.SendMessage;
import ru.javazen.telegram.bot.entity.Update;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

@Path("/hook")
public class HookService {
    private MessageService messageService;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void hook(Update update) {
        SendMessage message = new SendMessage();
        message.setChatId(update.getMessage().getChat().getId());
        message.setText(update.getMessage().getText());
        messageService.send(message);
    }

    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }
}
