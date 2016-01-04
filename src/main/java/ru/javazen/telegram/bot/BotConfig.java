package ru.javazen.telegram.bot;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class BotConfig {

    private final static String TOKEN_PROP_NAME = "bot.token";

    private final static String HOOK_URL_PROP_NAME = "bot.hook_url";

    private InputStream is;
    private Properties property = new Properties();

    public BotConfig() {
        try {
            is = BotConfig.class.getClassLoader().getResourceAsStream("config.properties");
            property.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getToken() {
        return property.getProperty(TOKEN_PROP_NAME);
    }

    public String getHookUrl() {
        return property.getProperty(HOOK_URL_PROP_NAME);
    }
}
