package ru.javazen.telegram.bot.service;

import java.util.Optional;

public interface ChatConfigService {

    Optional<String> getProperty(long chatId, String key);

    void setProperty(long chatId, String key, String value);
}
