package ru.javazen.telegram.bot.service;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import ru.javazen.telegram.bot.entity.request.Message;
import ru.javazen.telegram.bot.entity.request.User;
import ru.javazen.telegram.bot.entity.response.ForwardMessage;
import ru.javazen.telegram.bot.entity.response.SendMessage;
import ru.javazen.telegram.bot.entity.response.SendSticker;

import javax.ws.rs.core.MediaType;

@Service
@Scope(scopeName = "prototype")
public class TelegramBotServiceImpl implements TelegramBotService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TelegramBotServiceImpl.class);
    private static final String TELEGRAM_URL = "https://api.telegram.org/";

    @Autowired
    private Client client;
    private WebResource webResource;

    public TelegramBotServiceImpl(String token) {
        webResource = client.resource(TELEGRAM_URL).path("bot" + token);
    }

    @Override
    public void setWebHook(String url) {
        LOGGER.debug("setWebhook. url: {}", url);
        ClientResponse response = webResource
                .path("setWebhook")
                .queryParam("url", url)
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .post(ClientResponse.class);
        assertResponse(response);
    }

    @Override
    public User getMe() {
        LOGGER.debug("getMe");
        ClientResponse response = webResource
                .path("getMe")
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .get(ClientResponse.class);
        assertResponse(response);
        return response.getEntity(new GenericType<User>(User.class));
    }

    @Override
    public Message sendMessage(SendMessage message) {
        LOGGER.debug("sendMessage: {}", message);
        ClientResponse response = callMethod("sendMessage", message);
        assertResponse(response);
        return response.getEntity(new GenericType<Message>(Message.class));
    }

    @Override
    public Message forwardMessage(ForwardMessage message) {
        LOGGER.debug("forwardMessage: {}", message);
        ClientResponse response = callMethod("forwardMessage", message);
        assertResponse(response);
        return response.getEntity(new GenericType<Message>(Message.class));
    }

    @Override
    public Message sendSticker(SendSticker sticker) {
        LOGGER.debug("sendSticker: {}", sticker);
        ClientResponse response = callMethod("sendSticker", sticker);
        assertResponse(response);
        return response.getEntity(new GenericType<Message>(Message.class));
    }

    private void assertResponse(ClientResponse response) {
        if (response.getStatus() == ClientResponse.Status.OK.getStatusCode()){
            throw new RuntimeException(response.toString());
        }
    }

    private ClientResponse callMethod(String method, Object entity) {
        return webResource.path(method)
                .entity(entity, MediaType.APPLICATION_JSON_TYPE)
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .post(ClientResponse.class);
    }
}
