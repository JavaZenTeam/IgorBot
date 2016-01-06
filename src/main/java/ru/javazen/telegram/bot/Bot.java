package ru.javazen.telegram.bot;

import ru.javazen.telegram.bot.entity.Update;

public abstract class Bot {
    private String token;

    public abstract void onStart();
    public abstract void onUpdate(Update update);

    public Bot(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

}
