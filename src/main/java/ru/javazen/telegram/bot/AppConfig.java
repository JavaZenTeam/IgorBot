package ru.javazen.telegram.bot;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.javazen.telegram.bot.comparator.RandomComparator;

import java.util.Random;

@Configuration
public class AppConfig {

    @Bean(destroyMethod = "destroy")
    public Client client(){
        ClientConfig clientConfig = new DefaultClientConfig();
        clientConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
        return Client.create(clientConfig);
    }

    @Bean
    public WebResource telegramWebResource(){
        return client().resource("https://api.telegram.org/");
    }

    @Bean
    public Random random(){
        return new Random();
    }

    @Bean
    public RandomComparator randomComparator(){
        return new RandomComparator();
    }
}
