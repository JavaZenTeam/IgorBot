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
import net.sf.junidecode.Junidecode;
import org.apache.commons.codec.language.DoubleMetaphone;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.util.StringUtils;
import org.telegram.telegrambots.ApiContext;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.facilities.TelegramHttpClientBuilder;
import ru.javazen.telegram.bot.client.FileServiceClient;
import ru.javazen.telegram.bot.comparator.RandomComparator;
import ru.javazen.telegram.bot.handler.SayTextHandler;
import ru.javazen.telegram.bot.handler.base.InlineQueryHandler;
import ru.javazen.telegram.bot.repository.MessageTaskRepository;
import ru.javazen.telegram.bot.repository.VoiceMessageRepository;
import ru.javazen.telegram.bot.scheduler.service.MessageSchedulerService;
import ru.javazen.telegram.bot.scheduler.service.MessageSchedulerServiceImpl;
import ru.javazen.telegram.bot.service.VoiceService;
import ru.javazen.telegram.bot.service.impl.VoiceServiceImpl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.function.Function;


@Configuration
@EnableCaching
@EnableScheduling
public class AppConfig {

    /*@Bean(destroyMethod = "destroy")
    public Client client(){
        ClientConfig clientConfig = new DefaultClientConfig();
        clientConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
        return Client.create(clientConfig);
    }*/

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
        return new ConcurrentTaskScheduler();
    }

    @Bean
    public MessageSchedulerService messageSchedulerService(CompositeBot compositeBot,
                                                           MessageTaskRepository messageTaskRepository) {
        return new MessageSchedulerServiceImpl(compositeBot, messageTaskRepository);
    }

    @Bean
    public DefaultBotOptions proxyBotOptions(
            @Value("${http.proxy.url}") String proxyUrl) throws MalformedURLException {
        DefaultBotOptions botOptions = ApiContext.getInstance(DefaultBotOptions.class);

        if (!StringUtils.isEmpty(proxyUrl)) {
            URL url = new URL(proxyUrl);
            String proxyHost = url.getHost();
            int proxyPort = url.getPort();
            HttpHost httpHost = new HttpHost(proxyHost, proxyPort);

            String userInfo = url.getUserInfo();
            boolean isAuth = !StringUtils.isEmpty(userInfo);
            if (isAuth) {
                StringTokenizer tokenizer = new StringTokenizer(userInfo, ":");
                String username = tokenizer.nextToken();
                String password = tokenizer.nextToken();

                CredentialsProvider credsProvider = new BasicCredentialsProvider();
                credsProvider.setCredentials(
                        new AuthScope(proxyHost, proxyPort),
                        new UsernamePasswordCredentials(username, password));
                botOptions.setCredentialsProvider(credsProvider);

            }

            RequestConfig requestConfig = RequestConfig.custom()
                    .setProxy(httpHost)
                    .setAuthenticationEnabled(isAuth)
                    .build();

            botOptions.setRequestConfig(requestConfig);

            botOptions.setHttpProxy(httpHost);
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
    VoiceService voiceService(FileServiceClient fileServiceClient, AmazonPolly amazonClient, VoiceMessageRepository voiceMessageRepository) {
        return new VoiceServiceImpl(fileServiceClient, amazonClient, voiceMessageRepository);
    }

    @Bean
    @Profile("say-text")
    SayTextHandler sayTextHandler(VoiceService voiceService) {
        return new SayTextHandler(voiceService);
    }

    @Bean("sayTextHandler")
    @Profile("!say-text")
    InlineQueryHandler sayTextHandlerStub() {
        return (inlineQuery, sender) -> false;
    }

    @Bean
    HttpClient httpClient(DefaultBotOptions options) {
        return TelegramHttpClientBuilder.build(options);
    }


}
