package ru.javazen.telegram.bot.help;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.javazen.telegram.bot.handler.base.CallbackQueryHandler;
import ru.javazen.telegram.bot.util.MessageData;

@RequiredArgsConstructor
@Component("helpMainPostQueryHandler")
public class HelpMainPostQueryHandler implements CallbackQueryHandler {

    private final HelpInfoProvider helpInfoProvider;

    @Override
    public boolean handle(CallbackQuery callbackQuery, AbsSender sender) throws TelegramApiException {

        MessageData helpMessage = helpInfoProvider.getHelpMessage();

        EditMessageText editedMessage = new EditMessageText()
                .setParseMode("MARKDOWN")
                .setChatId(callbackQuery.getMessage().getChatId())
                .setMessageId(callbackQuery.getMessage().getMessageId())
                .setText(helpMessage.getMessageText())
                .setReplyMarkup(helpMessage.getInlineKeyboardMarkup());

        sender.execute(editedMessage);
        return true;
    }
}
