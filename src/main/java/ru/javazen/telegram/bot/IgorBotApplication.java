package ru.javazen.telegram.bot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;
import org.telegram.telegrambots.ApiContextInitializer;

@SpringBootApplication
@ImportResource("classpath:spring/app.xml")
public class IgorBotApplication {
    public static void main(String[] args) {

        ApiContextInitializer.init();

        SpringApplication.run(IgorBotApplication.class, args);
    }
}