package ru.javazen.telegram.bot;

import ru.javazen.telegram.bot.entity.SendMessage;
import ru.javazen.telegram.bot.entity.Update;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

@Path("/hook")
public class HookService {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void hook(Update update) {

        MessageService.send(new SendMessage(update.getMessage().getChat().getId(), update.getMessage().getText()));
    }
}
