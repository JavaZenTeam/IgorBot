package ru.javazen.telegram.bot;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.javazen.telegram.bot.entity.SendMessage;

import javax.ws.rs.core.MediaType;

//TODO
public class MessageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageService.class);

    private String token;
    private String hookUrl;

    private final static String SEND_MESSAGE_METHOD = "sendMessage";

    public void send(SendMessage message) {
        ClientConfig clientConfig = new DefaultClientConfig();
        clientConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);

        Client client = Client.create(clientConfig);

        WebResource service = client.resource("https://api.telegram.org/"); //TODO

        ClientResponse response = service.path("bot" + token)
                .path(SEND_MESSAGE_METHOD)
                .queryParam("url", hookUrl)
                .entity(message, MediaType.APPLICATION_JSON_TYPE)
                .post(ClientResponse.class);

        if (response.getStatus() != 200) {
            LOGGER.warn("Error when sending messages: " + response.getEntity(String.class));
        }

        client.destroy();
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setHookUrl(String hookUrl) {
        this.hookUrl = hookUrl;
    }
}
