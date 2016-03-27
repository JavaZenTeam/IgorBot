package ru.javazen.telegram.bot.method;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import ru.javazen.telegram.bot.entity.response.ForwardMessage;

import javax.ws.rs.core.MediaType;

public class ForwardMessageMethod extends TelegramMethod<ForwardMessage> {

    public ForwardMessageMethod(ForwardMessage entity) {
        super(entity);
    }

    @Override
    public ClientResponse execute(WebResource webResource) {
        return webResource.path("forwardMessage")
                .entity(getEntity(), MediaType.APPLICATION_JSON_TYPE)
                .post(ClientResponse.class);
    }
}
