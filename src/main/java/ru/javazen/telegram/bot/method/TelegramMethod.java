package ru.javazen.telegram.bot.method;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public abstract class TelegramMethod<T> {
    private T entity;

    public TelegramMethod() {
    }

    public TelegramMethod(T entity) {
        this.entity = entity;
    }

    public T getEntity() {
        return entity;
    }

    public void setEntity(T entity) {
        this.entity = entity;
    }

    public abstract ClientResponse execute(WebResource webResource);
}
