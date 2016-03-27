package ru.javazen.telegram.bot.service;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.javazen.telegram.bot.method.TelegramMethod;

@Service
public class TelegramService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CallbackService.class);

    @Autowired
    @Qualifier("telegramWebResource")
    private WebResource telegramWebResource;

    public boolean execute(TelegramMethod method, String token){
        LOGGER.debug("Start execute method {}", method);

        WebResource temp = telegramWebResource.path("bot" + token);
        ClientResponse response = method.execute(temp);

        boolean result = response.getStatus() == ClientResponse.Status.OK.getStatusCode();
        if (result) {
            LOGGER.debug("Successfully execute method {}", method);
        } else {
            LOGGER.warn("Failed to execute method {}. Cause: {}", method, response.getEntity(String.class));
        }
        return result;
    }
}
