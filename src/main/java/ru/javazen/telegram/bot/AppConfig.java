package ru.javazen.telegram.bot;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.polly.AmazonPolly;
import com.amazonaws.services.polly.AmazonPollyClientBuilder;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;
import net.sf.junidecode.Junidecode;
import org.apache.commons.codec.language.DoubleMetaphone;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Scope;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.facilities.TelegramHttpClientBuilder;
import ru.javazen.telegram.bot.client.FileServiceClient;
import ru.javazen.telegram.bot.handler.SayTextHandler;
import ru.javazen.telegram.bot.handler.base.InlineQueryHandler;
import ru.javazen.telegram.bot.service.AudioConverterService;
import ru.javazen.telegram.bot.service.VoiceService;
import ru.javazen.telegram.bot.service.impl.AudioConverterServiceImpl;
import ru.javazen.telegram.bot.service.impl.VoiceServiceImpl;
import ru.javazen.telegram.bot.util.comparator.RandomComparator;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.function.Function;

@Configuration
@EnableCaching
@EnableScheduling
@Slf4j
public class AppConfig {

    @Bean
    @Scope("prototype")
    public Random random() {
        return new Random();
    }

    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager("ChatConfig", "DictionaryWord");
    }

    @Bean
    public RandomComparator randomComparator() {
        return new RandomComparator(random());
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.setVisibility(
                mapper.getSerializationConfig().getDefaultVisibilityChecker()
                        .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                        .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                        .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
                        .withCreatorVisibility(JsonAutoDetect.Visibility.NONE)
        );
        return mapper;
    }

    @Bean
    public DoubleMetaphone doubleMetaphone() {
        DoubleMetaphone metaphone = new DoubleMetaphone();
        metaphone.setMaxCodeLen(30);
        return metaphone;
    }

    @Bean
    public Function<String, String> songEncoder() {
        return s -> doubleMetaphone().encode(Junidecode.unidecode(s));
    }

    @Bean
    public TaskScheduler taskScheduler() {
        return new ConcurrentTaskScheduler(Executors.newScheduledThreadPool(10));
    }

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new ResponseErrorHandler() {
            @Override
            public boolean hasError(ClientHttpResponse response) throws IOException {
                return response.getStatusCode().isError();
            }

            @Override
            public void handleError(ClientHttpResponse response) throws IOException {
                // Do nothing, we want to handle the error ourselves
            }
        });
        return restTemplate;
    }

    @Bean
    public DefaultBotOptions proxyBotOptions(
            @Value("${http.proxy.url:}") String proxyUrl) throws MalformedURLException {
        DefaultBotOptions botOptions = new DefaultBotOptions();
        if (proxyUrl != null && !proxyUrl.isBlank()) {
            URL url = new URL(proxyUrl);
            String protocol = url.getProtocol();
            if ("socks".equalsIgnoreCase(protocol) || "socks5".equalsIgnoreCase(protocol)) {
                botOptions.setProxyType(DefaultBotOptions.ProxyType.SOCKS5);
            } else {
                botOptions.setProxyType(DefaultBotOptions.ProxyType.HTTP);
            }
            botOptions.setProxyHost(url.getHost());
            int port = url.getPort();
            if (port == -1) {
                port = "https".equalsIgnoreCase(protocol) ? 443 : 80;
            }
            botOptions.setProxyPort(port);
            String userInfo = url.getUserInfo();
            if (userInfo != null && !userInfo.isEmpty()) {
                String[] parts = userInfo.split(":", 2);
                CredentialsProvider credsProvider = new BasicCredentialsProvider();
                credsProvider.setCredentials(
                        new AuthScope(url.getHost(), port),
                        new UsernamePasswordCredentials(parts[0], parts.length > 1 ? parts[1] : "")
                );
                ((HttpClientContext) botOptions.getHttpContext()).setCredentialsProvider(credsProvider);
            }
        }
        return botOptions;
    }

    @Bean
    @Profile("say-text")
    FileServiceClient fileServiceClient(@Value("${file-service.url}") String fileServiceUrl) {
        return new FileServiceClient(fileServiceUrl);
    }

    @Bean
    @Profile("say-text")
    AmazonPolly amazonPolly(@Value("${polly.access-key}") String accessKey,
                            @Value("${polly.secret-key}") String secretKey) {
        AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
        AWSStaticCredentialsProvider provider = new AWSStaticCredentialsProvider(credentials);
        return AmazonPollyClientBuilder.standard()
                .withCredentials(provider)
                .withRegion("eu-west-2")
                .build();
    }

    @Bean
    @Profile("say-text")
    AudioConverterService audioConverterService() {
        return new AudioConverterServiceImpl();
    }

    @Bean
    @Profile("say-text")
    VoiceService voiceService(FileServiceClient fileServiceClient, AmazonPolly amazonClient, AudioConverterService audioConverterService) {
        return new VoiceServiceImpl(fileServiceClient, amazonClient, audioConverterService);
    }

    @Bean
    @Profile("say-text")
    SayTextHandler sayTextHandler(VoiceService voiceService, TaskScheduler taskScheduler) {
        return new SayTextHandler(voiceService, taskScheduler);
    }

    @Bean("sayTextHandler")
    @Profile("!say-text")
    InlineQueryHandler sayTextHandlerStub() {
        return (inlineQuery, sender) -> false;
    }

    @Bean
    HttpClient telegramHttpClient(DefaultBotOptions options) {
        return TelegramHttpClientBuilder.build(options);
    }
}
