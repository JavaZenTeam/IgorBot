package ru.javazen.telegram.bot;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MediaType;

public class BotInitializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(BotInitializer.class);

    private String token;
    private String hookUrl;

    public void init() {
        Client client = Client.create();

        WebResource service = client.resource("https://api.telegram.org/");

        ClientResponse response = service.path("bot" + token)
                .path("setWebhook")
                .queryParam("url", hookUrl)
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .get(ClientResponse.class);
        if (response.getStatus() == 200) {
            System.out.println("Success in setting WebHook");
            LOGGER.info("WebHook is set");
        } else {
            LOGGER.error("Error when setting WebHook: " + response.getEntity(String.class));
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
