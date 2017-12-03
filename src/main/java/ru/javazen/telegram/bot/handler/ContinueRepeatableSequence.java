package ru.javazen.telegram.bot.handler;

import ru.javazen.telegram.bot.BotMethodExecutor;
import ru.javazen.telegram.bot.entity.Update;
import ru.javazen.telegram.bot.util.MessageHelper;

public class ContinueRepeatableSequence implements UpdateHandler {
    private volatile Update lastUpdate;

    @Override
    public boolean handle(Update update, BotMethodExecutor executor) {
        if (lastUpdate != null){
            boolean textEquals = update.getMessage().getText().equals(lastUpdate.getMessage().getText());
            boolean fromEquals = update.getMessage().getFrom().getId().equals(lastUpdate.getMessage().getFrom().getId());

            if (textEquals && !fromEquals){
                executor.execute(MessageHelper.answer(update.getMessage(), update.getMessage().getText()), Void.class);
                return true;
            }
        }
        lastUpdate = update;
        return false;
    }
}
