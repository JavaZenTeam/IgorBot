package ru.javazen.telegram.bot;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;
import ru.javazen.telegram.bot.entity.SendMessage;

import javax.ws.rs.core.MediaType;

//TODO
public class MessageService {

    private final static String SEND_MESSAGE_METHOD = "sendMessage";

    private final static BotConfig config = new BotConfig(); //TODO DI

    public static void send(SendMessage message) {
        ClientConfig clientConfig = new DefaultClientConfig();
        clientConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);

        Client client = Client.create(clientConfig);

        WebResource service = client.resource("https://api.telegram.org/"); //TODO

        ClientResponse response = service.path("bot" + config.getToken())
                .path(SEND_MESSAGE_METHOD)
                .queryParam("url", config.getHookUrl())
                .entity(message, MediaType.APPLICATION_JSON_TYPE)
                .post(ClientResponse.class);

        if (response.getStatus() != 200) {
            System.out.println("error in sending message"); //TODO logging
            System.out.println(response.getEntity(String.class));
        }

        client.destroy();
    }
}
