package ru.javazen.telegram.bot.method;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import javax.ws.rs.core.MediaType;

public class SetWebHookMethod extends TelegramMethod<String> {
    public SetWebHookMethod(String entity) {
        super(entity);
    }

    @Override
    public ClientResponse execute(WebResource webResource) {
        return webResource.path("setWebhook")
                .queryParam("url", getEntity())
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .get(ClientResponse.class);
    }
}
