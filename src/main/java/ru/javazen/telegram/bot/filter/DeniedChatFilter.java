package ru.javazen.telegram.bot.filter;

import org.telegram.telegrambots.api.objects.Update;

import java.util.ArrayList;
import java.util.List;

public class DeniedChatFilter implements Filter {

    private List<Long> deniedChatIds = new ArrayList<>();

    @Override
    public boolean check(Update update) {
        return !deniedChatIds.contains(update.getMessage().getChat().getId());
    }

    public void setDeniedChatIds(List<Long> deniedChatIds) {
        this.deniedChatIds = deniedChatIds;
    }
}
