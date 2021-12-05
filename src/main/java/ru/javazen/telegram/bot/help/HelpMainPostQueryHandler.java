package ru.javazen.telegram.bot.help;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
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

        EditMessageText editedMessage = EditMessageText.builder()
                .parseMode(ParseMode.MARKDOWN   )
                .chatId(callbackQuery.getMessage().getChatId().toString())
                .messageId(callbackQuery.getMessage().getMessageId())
                .text(helpMessage.getMessageText())
                .replyMarkup(helpMessage.getInlineKeyboardMarkup())
                .build();

        sender.execute(editedMessage);
        return true;
    }
}
