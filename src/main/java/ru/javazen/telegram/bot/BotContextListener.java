package ru.javazen.telegram.bot;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.ws.rs.core.MediaType;

@WebListener
public class BotContextListener implements ServletContextListener {

    private BotConfig config = new BotConfig();

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        Client client = Client.create();

        WebResource service = client.resource("https://api.telegram.org/");

        ClientResponse response = service.path("bot" + config.getToken())
                .path("setWebhook")
                .queryParam("url", config.getHookUrl())
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .get(ClientResponse.class);
        if (response.getStatus() == 200) {
            System.out.println("Success in setting WebHook");
        } else {
            System.out.println("Error in setting WebHook");
        }

        client.destroy();

        //TODO - logging
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        //TODO - logging
    }
}
