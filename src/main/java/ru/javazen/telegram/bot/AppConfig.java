package ru.javazen.telegram.bot;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;
import net.sf.junidecode.Junidecode;
import org.apache.commons.codec.language.DoubleMetaphone;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import ru.javazen.telegram.bot.comparator.RandomComparator;

import java.util.Random;
import java.util.function.Function;

@Configuration
public class AppConfig {

    @Bean(destroyMethod = "destroy")
    public Client client(){
        ClientConfig clientConfig = new DefaultClientConfig();
        clientConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
        return Client.create(clientConfig);
    }

    @Bean
    @Scope("prototype")
    public Random random(){
        return new Random();
    }

    @Bean
    public RandomComparator randomComparator(){
        return new RandomComparator(random());
    }

    @Bean
    public ObjectMapper objectMapper(){
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
        mapper.enable(SerializationConfig.Feature.INDENT_OUTPUT);
        mapper.setVisibilityChecker(
                mapper.getSerializationConfig().getDefaultVisibilityChecker()
                        .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                        .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                        .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
                        .withCreatorVisibility(JsonAutoDetect.Visibility.NONE)
        );
        return mapper;
    }

    @Bean
    public DoubleMetaphone doubleMetaphone(){
        DoubleMetaphone metaphone = new DoubleMetaphone();
        metaphone.setMaxCodeLen(30);
        return metaphone;
    }

    @Bean
    public Function<String, String> songEncoder(){
        return s -> doubleMetaphone().encode(Junidecode.unidecode(s));
    }
}
