package ru.javazen.telegram.bot.filter;


import ru.javazen.telegram.bot.entity.Update;

import java.util.ArrayList;
import java.util.List;

public class AllowedChatFilter implements Filter {

    private List<Long> allowedChatIds = new ArrayList<>();

    @Override
    public boolean check(Update update) {
        return allowedChatIds.contains(update.getMessage().getChat().getId());
    }

    public void setAllowedChatIds(List<Long> allowedChatIds) {
        this.allowedChatIds = allowedChatIds;
    }
}
