package ru.javazen.telegram.bot.filter;


import org.telegram.telegrambots.api.objects.Message;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AllowedChatFilter implements MessageFilter {

    private List<Long> allowedChatIds = new ArrayList<>();

    @Override
    public boolean check(Message message) {
        return allowedChatIds.contains(message.getChatId());
    }

    public void setAllowedChatIds(List<Long> allowedChatIds) {
        this.allowedChatIds = allowedChatIds;
    }

    public void setAllowedChatId(Long allowedChatId) {
        this.allowedChatIds = Collections.singletonList(allowedChatId);
    }
}
