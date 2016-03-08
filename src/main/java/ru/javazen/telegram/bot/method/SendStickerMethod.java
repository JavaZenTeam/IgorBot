package ru.javazen.telegram.bot.method;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import ru.javazen.telegram.bot.entity.response.SendSticker;

import javax.ws.rs.core.MediaType;

public class SendStickerMethod extends TelegramMethod<SendSticker>{
    public SendStickerMethod(SendSticker entity) {
        super(entity);
    }

    @Override
    public ClientResponse execute(WebResource webResource) {
        return webResource.path("sendSticker")
                .entity(getEntity(), MediaType.APPLICATION_JSON_TYPE)
                .post(ClientResponse.class);
    }
}
