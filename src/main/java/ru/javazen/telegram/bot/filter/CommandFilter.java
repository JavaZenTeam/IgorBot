package ru.javazen.telegram.bot.filter;

import lombok.AllArgsConstructor;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.MessageEntity;

@AllArgsConstructor
public class CommandFilter implements MessageFilter {
    private String botName;
    private String command;

    @Override
    public boolean check(Message message) {
        return message.getEntities() != null &&
                message.getEntities().stream()
                        .filter(me -> me.getType().equals("bot_command"))
                        .map(MessageEntity::getText)
                        .anyMatch(this::checkCommand);
    }

    private boolean checkCommand(String input) {
        String[] split = input.split("@");
        if (split.length == 1) {
            return command.equals(split[0]);
        } else {
            return command.equals(split[0]) && botName.equals(split[1]);
        }
    }
}
