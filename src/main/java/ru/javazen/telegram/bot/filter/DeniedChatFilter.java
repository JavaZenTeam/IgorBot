package ru.javazen.telegram.bot.filter;

import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.ArrayList;
import java.util.List;

public class DeniedChatFilter implements MessageFilter {

    private List<Long> deniedChatIds = new ArrayList<>();

    @Override
    public boolean check(Message message) {
        return !deniedChatIds.contains(message.getChatId());
    }

    public void setDeniedChatIds(List<Long> deniedChatIds) {
        this.deniedChatIds = deniedChatIds;
    }
}
