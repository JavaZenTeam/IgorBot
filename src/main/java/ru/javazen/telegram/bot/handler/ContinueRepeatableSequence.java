package ru.javazen.telegram.bot.handler;

import ru.javazen.telegram.bot.BotMethodExecutor;
import ru.javazen.telegram.bot.entity.Update;
import ru.javazen.telegram.bot.util.MessageHelper;

public class ContinueRepeatableSequence implements UpdateHandler {
    private volatile Update lastUpdate;

    @Override
    public boolean handle(Update update, BotMethodExecutor executor) {
        String text = update.getMessage().getText();
        if (lastUpdate != null && text != null){
            boolean textEquals = text.equals(lastUpdate.getMessage().getText());
            boolean fromEquals = update.getMessage().getFrom().getId().equals(lastUpdate.getMessage().getFrom().getId());

            if (textEquals && !fromEquals){
                executor.execute(MessageHelper.answer(update.getMessage(), text), Void.class);
                return true;
            }
        }
        lastUpdate = update;
        return false;
    }
}
