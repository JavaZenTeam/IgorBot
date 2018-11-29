package ru.javazen.telegram.bot.help;

import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.javazen.telegram.bot.handler.base.CallbackQueryHandler;
import ru.javazen.telegram.bot.util.MessageData;

@RequiredArgsConstructor
public class HelpFeatureCallbackHandler implements CallbackQueryHandler {

    private final HelpInfoProvider helpInfoProvider;
    private final String callbackPrefix;

    @Override
    public boolean handle(CallbackQuery callbackQuery, AbsSender sender) throws TelegramApiException {

        String featureName = callbackQuery.getData().substring(callbackPrefix.length());

        MessageData messageData = helpInfoProvider.getFeatureInformation(featureName);
        if (messageData != null) {
            EditMessageText editedMessage = new EditMessageText()
                    .setParseMode("MARKDOWN")
                    .setChatId(callbackQuery.getMessage().getChatId())
                    .setMessageId(callbackQuery.getMessage().getMessageId())
                    .setText(messageData.getMessageText())
                    .setReplyMarkup(messageData.getInlineKeyboardMarkup());

            sender.execute(editedMessage);
            return true;
        }
        return false;
    }
}
