package ru.javazen.telegram.bot;

import ru.javazen.telegram.bot.method.ApiMethod;

public class BotMethodExecutorStub extends BotMethodExecutor {
    private ApiMethod apiMethod;

    public BotMethodExecutorStub() {
        super(null, null);
    }

    @Override
    public <T> T execute(ApiMethod apiMethod, Class<T> clazz) {
        this.apiMethod = apiMethod;
        return null;
    }

    public ApiMethod getApiMethod() {
        return apiMethod;
    }
}
