package ru.javazen.telegram.bot.filter;


import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;

public class AllowedUserFilter implements MessageFilter {

    private List<Long> allowedUserIds;

    @Override
    public boolean check(Message message) {
        return allowedUserIds.contains(message.getFrom().getId().longValue());
    }

    public List<Long> getAllowedUserIds() {
        return allowedUserIds;
    }

    public void setAllowedUserIds(List<Long> allowedUserIds) {
        this.allowedUserIds = allowedUserIds;
    }
}
