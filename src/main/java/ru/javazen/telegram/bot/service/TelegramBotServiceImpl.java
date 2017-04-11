package ru.javazen.telegram.bot.service;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import ru.javazen.telegram.bot.entity.request.Message;
import ru.javazen.telegram.bot.entity.request.User;
import ru.javazen.telegram.bot.entity.response.ForwardMessage;
import ru.javazen.telegram.bot.entity.response.SendMessage;
import ru.javazen.telegram.bot.entity.response.SendSticker;

import javax.annotation.PostConstruct;
import javax.ws.rs.core.MediaType;
import java.text.MessageFormat;
import java.util.Arrays;

import static javax.ws.rs.core.Response.Status.Family.CLIENT_ERROR;
import static javax.ws.rs.core.Response.Status.Family.SERVER_ERROR;

public class TelegramBotServiceImpl implements TelegramBotService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TelegramBotServiceImpl.class);
    private static final String TELEGRAM_URL = "https://api.telegram.org/";

    @Autowired
    private Client client;

    private WebResource webResource;
    private String token;

    @Autowired
    public TelegramBotServiceImpl(@Value("${bot.token}") String token) {
        this.token = token;
    }

    @PostConstruct
    private void init(){
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
    }

    @Override
    public User getMe() {
        LOGGER.debug("getMe");
        ClientResponse response = webResource
                .path("getMe")
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .get(ClientResponse.class);
        return response.getEntity(new GenericType<User>(User.class));
    }

    @Override
    public Message sendMessage(SendMessage message) {
        LOGGER.debug("sendMessage: {}", message);
        ClientResponse response = callMethod("sendMessage", message);
        return response.getEntity(new GenericType<Message>(Message.class));
    }

    @Override
    public Message forwardMessage(ForwardMessage message) {
        LOGGER.debug("forwardMessage: {}", message);
        ClientResponse response = callMethod("forwardMessage", message);
        return response.getEntity(new GenericType<Message>(Message.class));
    }

    @Override
    public Message sendSticker(SendSticker sticker) {
        LOGGER.debug("sendSticker: {}", sticker);
        ClientResponse response = callMethod("sendSticker", sticker);
        return response.getEntity(new GenericType<Message>(Message.class));
    }

    private ClientResponse callMethod(String method, Object entity) {
        ClientResponse response = webResource.path(method)
                .entity(entity, MediaType.APPLICATION_JSON_TYPE)
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .post(ClientResponse.class);

        assertResponse(method, response);
        return response;
    }

    private void assertResponse(String method, ClientResponse response) {
        if (Arrays.asList(CLIENT_ERROR, SERVER_ERROR).contains(response.getStatusInfo().getFamily())){
            String msg = MessageFormat.format("Call {0} failed with status {1}. Response: %s",
                    method, response.getStatus(), response.getEntity(String.class));
            LOGGER.error(msg);
            throw new RuntimeException(msg);
        }
    }
}
