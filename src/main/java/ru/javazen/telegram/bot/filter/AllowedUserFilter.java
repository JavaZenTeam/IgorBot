package ru.javazen.telegram.bot.filter;

import ru.javazen.telegram.bot.entity.request.Update;

import java.util.List;

public class AllowedUserFilter implements Filter {

    private List<Long> allowedUserIds;

    @Override
    public boolean check(Update update) {
        return allowedUserIds.contains(update.getMessage().getFrom().getId());
    }

    public List<Long> getAllowedUserIds() {
        return allowedUserIds;
    }

    public void setAllowedUserIds(List<Long> allowedUserIds) {
        this.allowedUserIds = allowedUserIds;
    }
}
