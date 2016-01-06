package ru.javazen.telegram.bot.method;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import ru.javazen.telegram.bot.entity.SendMessage;

import javax.ws.rs.core.MediaType;

public class SendMessageMethod extends TelegramMethod<SendMessage>{
    public SendMessageMethod(SendMessage entity) {
        super(entity);
    }

    @Override
    public ClientResponse execute(WebResource webResource) {
        return webResource.path("sendMessage")
                .entity(getEntity(), MediaType.APPLICATION_JSON_TYPE)
                .post(ClientResponse.class);
    }
}
